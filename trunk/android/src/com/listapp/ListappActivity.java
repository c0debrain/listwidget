/*******************************************************************************
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.listapp;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.turbomanage.listwidget.shared.proxy.NamedListProxy;
import com.turbomanage.listwidget.shared.service.ListwidgetRequestFactory;

/**
 * Main activity - requests all lists from the server and provides
 * a menu item to invoke the accounts activity.
 */
public class ListappActivity extends Activity implements OnItemClickListener {
	/**
	 * Tag for logging.
	 */
	private static final String TAG = "ListappActivity";

	private static final int LOGIN = 0;

	/**
	 * The current context.
	 */
	private Context mContext = this;

	private ListwidgetAdapter adapter;

	private ListView listView;

	private final int LOADING_DIALOG = 1;
	private final int ERR_DIALOG = -1;
	private String errDialogMsg;

	private AsyncTask taskInProgress;

	/**
	 * Begins the activity.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		adapter = new ListwidgetAdapter(this);
//		setListAdapter(adapter);
//		listView = getListView();
		setContentView(R.layout.appview);
		// must layout first
		listView = (ListView) findViewById(R.id.main);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
		// TODO two calls to setContentView?
		// moved from onResume
		final SharedPreferences prefs = Util.getSharedPreferences(mContext);
		String loggedInAccount = prefs.getString(Util.AUTH_COOKIE, null);
		if (loggedInAccount == null) {
			login();
		} else {
			setScreenContent(R.layout.appview);
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case LOGIN:
			final SharedPreferences prefs = Util.getSharedPreferences(mContext);
			String loggedInAccount = prefs.getString(Util.AUTH_COOKIE, null);
			if (loggedInAccount != null) {
				setScreenContent(R.layout.appview);
			}
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		// Invoke the Register activity
		menu.getItem(0).setIntent(new Intent(this, AccountsActivity.class));
		return true;
	}

	// Manage UI Screens
	private void fetchLists() {
		final SharedPreferences prefs = Util.getSharedPreferences(mContext);
		String loggedInAccount = prefs.getString(Util.AUTH_COOKIE, null);
		if (loggedInAccount == null) {
			login();
		} else {
			showDialog(LOADING_DIALOG);
			taskInProgress = new FetchListsTask(this).execute();
		}
	}

	/**
	 * Sets the screen content based on the screen id.
	 */
	private void setScreenContent(int screenId) {
		// TODO DONT DO THIS!
		// setContentView(screenId);
		switch (screenId) {
		case R.layout.appview:
			fetchLists();
			break;
		}
	}

	public void setLists(List<NamedListProxy> lists) {
		dismissDialog(LOADING_DIALOG);
		adapter.setLists(lists);
	}

	public void login() {
		startActivityForResult(new Intent(this, AccountsActivity.class), LOGIN);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			fetchLists();
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void showError(String message) {
		dismissDialog(LOADING_DIALOG);
		errDialogMsg = message;
		showDialog(ERR_DIALOG);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent viewListIntent = new Intent(this, ViewListActivity.class);
		viewListIntent.putExtra("position", position);
		NamedListProxy list = adapter.getItem(position);
		// TODO I do not like this, Sam-I-am
		ListwidgetRequestFactory requestFactory = Util.getRequestFactory(this, ListwidgetRequestFactory.class);
		String historyToken = requestFactory.getHistoryToken(list.stableId());
		viewListIntent.putExtra("listId", historyToken);
		startActivity(viewListIntent);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);

		switch (id) {
		case ERR_DIALOG:
			AlertDialog errDialog = new AlertDialog.Builder(this)
			// See http://code.google.com/p/android/issues/detail?id=6489
			.setMessage("")
			.setCancelable(false)
			.setNeutralButton("OK", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			})
			.create();
			return errDialog;

		case LOADING_DIALOG :
			ProgressDialog progressDialog = new ProgressDialog(mContext);
			progressDialog.setMessage("Loading...");
			progressDialog.setCancelable(true);
			progressDialog.setCanceledOnTouchOutside(false);
			// Listen for back button or cancel button in dialog itself
			progressDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {
					if (taskInProgress != null) {
						// TODO why doesn't this stop the HTTP request?
						taskInProgress.cancel(true);
					}
				}
			});
			return progressDialog;
			
		default:
			return null;
		}
	}
	
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		super.onPrepareDialog(id, dialog);
		switch (id) {
		case ERR_DIALOG:
			((AlertDialog) dialog).setMessage(errDialogMsg);
			break;

		default:
			break;
		}
	}
}
