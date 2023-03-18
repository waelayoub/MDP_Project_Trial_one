package com.example.campusconnectapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.campusconnectapp.databinding.ActivitySplashScreenBinding
import com.microsoft.graph.authentication.IAuthenticationProvider
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import java.util.concurrent.CompletableFuture


class SplashScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    companion object{
        private val SCOPES = arrayOf("Files.Read.All")
        private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
        private val TAG:String = SplashScreen::class.java.simpleName //bala ta3me mn chila later
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        PublicClientApplication.createSingleAccountPublicClientApplication(this.applicationContext,
                R.raw.auth_config_single_account,
                object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                    override fun onCreated(application: ISingleAccountPublicClientApplication?) {
                        mSingleAccountApp = application
                        loadAccount()
                    }

                    override fun onError(exception: MsalException?) {
                        Log.d(TAG, exception.toString())
                    }
                })

        binding.EnterButton.setOnClickListener {
            mSingleAccountApp?.signIn(this, null, SCOPES, getAuthInteractiveCallback())
        }

        GlobalScope.launch(Dispatchers.Main) {
            Thread.sleep(3000)
            binding.progressBar.visibility=View.INVISIBLE
            binding.EnterButton.visibility=View.VISIBLE
        }


    }


    // This is called when logging in for the first time
    // this will open the microsoft login screen and if all is good it will return the token
    private fun getAuthInteractiveCallback() : AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult?) {
                Log.d(TAG, "Successfully authenticated")
//                updateUI(authenticationResult!!.account) // here to change if i loaded the account
                if (authenticationResult!=null){
                    callGraphAPI(authenticationResult)
                }
            }

            override fun onError(exception: MsalException?) {
                Log.d(TAG, "Authentication failed Interactively: " + exception.toString())
            }

            override fun onCancel() {
                Log.d(TAG, "User cancelled login.")
            }
        }
    }

    // This is called when you are already logged in
    // this will return the token
    private fun getAuthSilentCallback(): SilentAuthenticationCallback {
        return object : SilentAuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult?) {
                Log.d(TAG, "Successfully authenticated")
                /* Successfully got a token, use it to call a protected resource - MSGraph */
                callGraphAPI(authenticationResult!!)
            }

            override fun onError(exception: MsalException?) {
                Log.d(TAG, "Authentication failed Silently: " + exception.toString())
            }
        }
    }


    // this will call authSilent
    private fun loadAccount(){
        if(mSingleAccountApp!=null){
            mSingleAccountApp?.getCurrentAccountAsync(
                object:ISingleAccountPublicClientApplication.CurrentAccountCallback{
                    override fun onAccountLoaded(activeAccount: IAccount?) {
                        if(activeAccount!=null){
//                            println(activeAccount.authority)
                            mSingleAccountApp!!.acquireTokenSilentAsync(
                                SCOPES, "https://login.microsoftonline.com/11a6c59d-5d73-4a47-b23e-f5fcf9bf009b", getAuthSilentCallback()
                            )
                        }

//                        updateUI(activeAccount) // here to change if i loaded the account
                    }
                    override fun onAccountChanged(
                        priorAccount: IAccount?,
                        currentAccount: IAccount?
                    ) {
                        Log.d(TAG,"Signed Out.")
                    }
                    override fun onError(exception: MsalException) {
                        Log.d(TAG,exception.toString())

                    }
                }
            )
        }
    }


    // this will return the information about the user from the token
    private fun callGraphAPI(authenticationResult:IAuthenticationResult){
        val accessToken:String = authenticationResult.accessToken
//        println("Access Token : "+ accessToken)
//        Log.d(TAG, "Problem here")
        val graphClient = GraphServiceClient.builder()
            .authenticationProvider(object : IAuthenticationProvider {
                override fun getAuthorizationTokenAsync(requestUrl: URL): CompletableFuture<String> {
                    Log.d(TAG, "Authenticating request," + requestUrl.toString())
                    val accessTokenFuture: CompletableFuture<String> = CompletableFuture()
                    accessTokenFuture.complete(accessToken)
                    return accessTokenFuture
                }
            }).buildClient()
//        Log.d(TAG, "Finally here")

        GlobalScope.launch(Dispatchers.IO){
            val result = graphClient.me().buildRequest().get()
//            Log.d(TAG, "Maybe here")
//            println(result?.displayName+"  N  ")
//            println(result?.mobilePhone+"  Ph  "+ result?.mail+"  Mail   ")

            val intent = Intent(applicationContext, Home_Activity::class.java)
            intent.putExtra("name", result?.displayName)
            intent.putExtra("id", result?.id.toString())
            intent.putExtra("mail", result?.mail)
            startActivity(intent)
            finish()



        }

    }


}