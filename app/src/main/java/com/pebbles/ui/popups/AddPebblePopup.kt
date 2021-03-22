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

class AddPebblePopup(context: Context, listener: PopupListener) :
    Dialog(context, R.style.DialogTheme) {


    private lateinit var pebbleKeyTextInputField: TextInputLayout
    private lateinit var pebbleNameTextInputField: TextInputLayout
    private lateinit var pebbleNameEditText: EditText
    private lateinit var pebblekeyEditText: EditText

    private lateinit var connectButton: Button
    private lateinit var cancelButton: Button

    private var popupListener: PopupListener? = null

    init {
        popupListener = listener
        initViews()
        this.show()
    }

    private fun initViews() {
        setContentView(R.layout.dialog_add_pebble)
        initDialog()
        bindViews()
        initializeButtons()
    }

    private fun initializeButtons() {
        cancelButton.setOnClickListener {
            this.dismiss()
            popupListener?.onCancelClicked()
        }
        connectButton.setOnClickListener {
            if(isEntriesValid()) {
                val name = pebbleNameEditText.text.toString().trim()
                val key = pebblekeyEditText.text.toString().trim()
                popupListener?.onNextClicked(name, key)
                this.dismiss()
            }
        }
    }
    //endregion

    //region Validation
    private fun isEntriesValid() :Boolean {
        val name = pebbleNameEditText.text.toString().trim()
        val key = pebblekeyEditText.text.toString().trim()

        if(name.isBlank()){
            pebbleNameTextInputField.error = "Enter a name"
            return false
        } else if(key.isBlank()) {
            pebbleKeyTextInputField.error = "Enter a key"
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
        pebbleKeyTextInputField = findViewById(R.id.pebbleKeyTextInputField)
        pebbleNameTextInputField = findViewById(R.id.pebbleNameTextInputField)
        pebbleNameEditText = findViewById(R.id.pebbleNameEditText)
        pebblekeyEditText = findViewById(R.id.pebblekeyEditText)
        connectButton = findViewById(R.id.connectButton)
        cancelButton = findViewById(R.id.cancelButton)
    }


    interface PopupListener {
        fun onCancelClicked()
        fun onNextClicked(name: String, key: String)
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