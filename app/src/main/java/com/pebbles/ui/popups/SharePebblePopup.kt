package com.pebbles.ui.popups

import android.app.Dialog
import android.content.Context
import android.view.MotionEvent
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import com.google.android.material.textfield.TextInputLayout
import com.pebbles.R
import kotlinx.android.synthetic.main.dialog_share_pebble.*

class SharePebblePopup(context: Context, listener: PopupListener) :
    Dialog(context, R.style.DialogTheme) {


    private lateinit var usernameTextInputField: TextInputLayout
    private lateinit var usernameEditText: EditText

    private lateinit var shareButton: Button
    private lateinit var cancelButton: Button

    private var popupListener: PopupListener? = null

    init {
        popupListener = listener
        initViews()
        this.show()
    }

    private fun initViews() {
        setContentView(R.layout.dialog_share_pebble)
        initDialog()
        bindViews()
        initializeButtons()
    }

    private fun initializeButtons() {
        cancelButton.setOnClickListener {
            this.dismiss()
            popupListener?.onCancelClicked()
        }
        shareButton.setOnClickListener {
            if(isEntriesValid()) {
                val name = usernameEditText.text.toString().trim()
                popupListener?.onNextClicked(name)
                this.dismiss()
            }
        }
    }
    //endregion

    //region Validation
    private fun isEntriesValid() :Boolean {
        val name = usernameEditText.text.toString().trim()

        if(name.isBlank()){
            usernameTextInputField.error = "Enter a username"
            return false
        } else {
            return true
        }
    }
    //endregion

    //region init dialog
    private fun initDialog() {
        val layoutParams = window?.attributes
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.attributes = layoutParams
        setCancelable(false)
    }

    private fun bindViews() {
        usernameTextInputField = findViewById(R.id.usernameTextInputField)
        usernameEditText = findViewById(R.id.usernameEditText)
        shareButton = findViewById(R.id.shareButton)
        cancelButton = findViewById(R.id.cancelButton)
    }


    interface PopupListener {
        fun onCancelClicked()
        fun onNextClicked(username: String)
    }

    //hiding soft keyboard on touch outside
    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
            currentFocus?.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
    //endregion
}