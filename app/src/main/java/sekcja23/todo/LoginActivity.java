package sekcja23.todo;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.Credentials;
import com.auth0.android.result.UserProfile;

public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private Lock lock = null;
    private Auth0 auth0 = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        lock = Lock.newBuilder(auth0, callback)
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                // Add parameters to the Lock Builder
                .build(this);

        startActivity(lock.newIntent(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Your own Activity code
        lock.onDestroy(this);
        lock = null;
    }

    private LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(Credentials credentials) {
            // Login Success response
            //setContentView(R.layout.activity_home);
            String idToken = credentials.getIdToken();
            UsersAPIClient usersClient = new UsersAPIClient(auth0, idToken);
            AuthenticationAPIClient authClient = new AuthenticationAPIClient(auth0);
            String accessToken = credentials.getAccessToken();
            authClient.userInfo(accessToken)
                    .start(new BaseCallback<UserProfile, AuthenticationException>() {

                        @Override
                        public void onSuccess(final UserProfile userInfo) {
                            Intent i = new Intent(getApplicationContext(), HomeActivity.class);
                            String userEmail = userInfo.getEmail();
                            i.putExtra("userEmail", userEmail);
                            startActivity(i);
                        }

                        @Override
                        public void onFailure(AuthenticationException error) {
                            //show error
                        }
                    });
        }

        @Override
        public void onCanceled() {
            // Login Cancelled response
        }

        @Override
        public void onError(LockException error){
            // Login Error response
        }
    };

/*
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
*/

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };
    }
}

