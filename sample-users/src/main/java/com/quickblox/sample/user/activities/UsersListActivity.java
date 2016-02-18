package com.quickblox.sample.user.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.sample.core.utils.Toaster;
import com.quickblox.sample.user.R;
import com.quickblox.sample.user.adapter.UserListAdapter;
import com.quickblox.sample.user.helper.DataHolder;
import com.quickblox.users.QBUsers;

import static com.quickblox.sample.user.definitions.Consts.POSITION;

public class UsersListActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private UserListAdapter usersListAdapter;
    private ListView usersList;
    private Button logOutButton;
    private Button signInButton;
    private Button selfEditButton;
    private Button singUpButton;

    public static void start(Context context) {
        Intent intent = new Intent(context, UsersListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        initUI();
        initUsersList();
    }

    private void initUI() {
        logOutButton = (Button) findViewById(R.id.logout_button);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        selfEditButton = (Button) findViewById(R.id.self_edit_button);
        singUpButton = (Button) findViewById(R.id.sign_up_button);
        usersList = (ListView) findViewById(R.id.users_listview);
    }

    private void initUsersList() {
        usersListAdapter = new UserListAdapter(this);
        usersList.setAdapter(usersListAdapter);
        usersList.setOnItemClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DataHolder.getDataHolder().getSignInQbUser() != null) {
            signInButton.setVisibility(View.GONE);
            singUpButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.VISIBLE);
            selfEditButton.setVisibility(View.VISIBLE);
        }
        usersListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // destroy session after app close
        DataHolder.getDataHolder().setSignInQbUser(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            signInButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
        case R.id.sign_in_button:
            intent = new Intent(this, SignInActivity.class);
            startActivityForResult(intent, 0);
            break;

        case R.id.sign_up_button:
            intent = new Intent(this, SignUpUserActivity.class);
            startActivity(intent);
            break;

        case R.id.logout_button:
            progressDialog.show();

            // Logout
            //
            QBUsers.signOut(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void result, Bundle bundle) {
                    progressDialog.hide();

                    Toaster.longToast(R.string.user_log_out_msg);
                    updateDataAfterLogOut();
                }

                @Override
                public void onError(QBResponseException e) {
                    progressDialog.hide();

                    Toaster.longToast(e.getErrors().toString());
                }
            });
            break;

        case R.id.self_edit_button:
            intent = new Intent(this, UpdateUserActivity.class);
            startActivity(intent);
            break;
        }
    }

    private void updateDataAfterLogOut() {
        DataHolder.getDataHolder().setSignInQbUser(null);
        signInButton.setVisibility(View.VISIBLE);
        logOutButton.setVisibility(View.GONE);
        selfEditButton.setVisibility(View.GONE);
        singUpButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        startShowUserActivity(position);
    }

    private void startShowUserActivity(int position) {
        Intent intent = new Intent(this, ShowUserActivity.class);
        intent.putExtra(POSITION, position);
        startActivity(intent);
    }
}