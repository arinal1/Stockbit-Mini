package com.arinal.ui.account.support

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import com.arinal.R
import com.arinal.databinding.FragmentSupportBinding
import com.arinal.ui.account.AccountViewModel
import com.arinal.ui.base.BaseFragment
import im.crisp.sdk.Crisp
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URISyntaxException
import java.net.URLDecoder
import java.text.SimpleDateFormat
import java.util.*

class SupportFragment : BaseFragment<FragmentSupportBinding, AccountViewModel>() {

    private var cameraPhotoPath = ""
    private var filePathCallback: ValueCallback<Array<Uri>>? = null
    override val viewModel: AccountViewModel by sharedViewModel()
    override fun setLayout() = R.layout.fragment_support
    override fun observeLiveData() = Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        viewModel.title.value = getString(R.string.support)
        viewModel.showBack.value = true
        viewModel.showProgress.value = false
        initCrisp(savedInstanceState)
        return view
    }

    private fun initCrisp(savedInstanceState: Bundle?) {
        mWebView = binding.webView
        setUpWebViewDefaults(mWebView)

        if (savedInstanceState != null) mWebView?.restoreState(savedInstanceState)

        mWebView?.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                isLoaded = true
                flushQueue()
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (!url.startsWith("mailto")) false
                else {
                    handleMailToLink(url)
                    true
                }
            }

            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                return when {
                    url.startsWith("mailto") -> {
                        handleMailToLink(url)
                        true
                    }
                    url.startsWith("tel:") -> {
                        handleTelToLink(url)
                        true
                    }
                    url.startsWith("intent") -> {
                        handleIntentToLink(url)
                        false
                    }
                    else -> false
                }
            }
        }

        mWebView?.webChromeClient = object : WebChromeClient() {
            @SuppressLint("QueryPermissionsNeeded")
            override fun onShowFileChooser(
                webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                if (this@SupportFragment.filePathCallback != null) this@SupportFragment.filePathCallback?.onReceiveValue(null)
                this@SupportFragment.filePathCallback = filePathCallback
                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent?.resolveActivity(requireActivity().packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", cameraPhotoPath)
                    } catch (ex: IOException) {
                    }

                    if (photoFile == null) takePictureIntent = null
                    else {
                        cameraPhotoPath = "file:" + photoFile.absolutePath
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile)
                        )
                    }
                }
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"
                val intentArray: Array<Intent?>
                intentArray = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)
                return true
            }
        }
        mWebView?.loadUrl("file:///android_asset/index.html")
        load()
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        @Suppress("DEPRECATION") val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebViewDefaults(webView: WebView?) {
        webView?.settings?.apply {
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            databaseEnabled = true
            domStorageEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
        }
        WebView.setWebContentsDebuggingEnabled(true)
        mWebView?.webViewClient = WebViewClient()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || filePathCallback == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        var results: Array<Uri>? = null

        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                if (cameraPhotoPath != "") results = arrayOf(Uri.parse(cameraPhotoPath))
            } else {
                val dataString = data.dataString
                if (dataString != null) results = arrayOf(Uri.parse(dataString))
            }
        }
        filePathCallback?.onReceiveValue(results)
        filePathCallback = null
        return
    }

    override fun onDetach() {
        isLoaded = false
        super.onDetach()
    }

    private fun handleIntentToLink(url: String?) {
        try {
            val intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            startActivity(intent)
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
    }

    private fun handleTelToLink(url: String?) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }

    private fun handleMailToLink(url: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.data = Uri.parse("mailto:")
        intent.type = "plain/text"

        val to = url.split("[:?]".toRegex()).toTypedArray()[1]
        if (!TextUtils.isEmpty(to)) intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(to))

        if (url.contains("subject=")) {
            val subject = url.split("subject=".toRegex()).toTypedArray()[1]
            if (!TextUtils.isEmpty(subject)) intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        }

        if (url.contains("body=")) {
            var body = url.split("body=".toRegex()).toTypedArray()[1]
            if (!TextUtils.isEmpty(body)) {
                body = body.split("&".toRegex()).toTypedArray()[0]
                try {
                    body = URLDecoder.decode(body, "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
                intent.putExtra(Intent.EXTRA_TEXT, body)
            }
        }
        startActivity(intent)
    }

    companion object {
        const val INPUT_FILE_REQUEST_CODE = 1
        var mWebView: WebView? = null
        private val commandQueue = LinkedList<String>()
        var isLoaded = false
        private fun callJavascript(script: String) {
            mWebView?.evaluateJavascript(script, null)
        }

        fun flushQueue() {
            val iterator: Iterator<*> = commandQueue.iterator()
            while (iterator.hasNext()) {
                val script = iterator.next() as String
                callJavascript(script)
            }
            commandQueue.clear()
        }

        fun load() {
            if (Crisp.getInstance().tokenId != null && Crisp.getInstance().tokenId.isNotEmpty()) {
                execute("window.CRISP_TOKEN_ID = \"" + Crisp.getInstance().tokenId + "\";")
            }
            if (Crisp.getInstance().websiteId != null) {
                execute("window.CRISP_WEBSITE_ID = \"" + Crisp.getInstance().websiteId + "\";")
            }
            if (Crisp.getInstance().locale != null) {
                execute("window.CRISP_RUNTIME_CONFIG.locale = \"" + Crisp.getInstance().locale + "\";")
            }
            execute("initialize()")
        }

        private fun execute(script: String) {
            commandQueue.add(script)
            if (isLoaded) flushQueue()
        }
    }

}
