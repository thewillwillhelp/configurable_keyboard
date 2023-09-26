package com.personalapp.configurable_keyboard

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children

class ConfigurationActivity : AppCompatActivity() {
    var list: RadioGroup? = null;
    var mTextEdit: EditText? = null;

    class RadioBtnClikListener(context: Context, optionValue: Int): View.OnClickListener {
        var mContext = context;
        var mOptionValue: Int = optionValue;

        override fun onClick(v: View?) {
            val list: RadioGroup = v!!.parent as RadioGroup

            Utils.setActiveKeyboard(mContext, mOptionValue);

            for (configOption in list!!.children) {
                (configOption as RadioButton).isChecked = false
            }

            (v as RadioButton).isChecked = true

            (mContext as ConfigurationActivity).closeConfig()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.configuration_layout)

        mTextEdit = findViewById<EditText>(R.id.text_edit)

        var saveBtn = findViewById<Button>(R.id.save_btn)
        saveBtn.setOnClickListener(View.OnClickListener { saveConfig() })

        var resetBtn = findViewById<Button>(R.id.reset_btn)
        resetBtn.setOnClickListener(View.OnClickListener { resetConfig() })
        resetBtn.setBackgroundColor(Color.parseColor("#FFBB4444"))

        val configs = Utils.getConfigs(this)

        displayConfigText(configs)
        displayConfigsList(configs)
    }

    fun closeConfig() {
        this.finish()
    }

    fun displayConfigsList(configs: KeyboardConfig) {
        list = findViewById<RadioGroup>(R.id.configs_list)
        list!!.removeAllViews()

        var i = 0;
        for (keyboard in configs.layouts) {
            val btnView = RadioButton(this)
            btnView.setText(keyboard.name)
            btnView.isChecked = i == configs.active
            list!!.addView(btnView)
            btnView.setOnClickListener(RadioBtnClikListener(this, i))

            i++;
        }
    }

    fun displayConfigText(configs: KeyboardConfig) {
        val configString = Utils.convertConfigToJson(configs)
        mTextEdit?.setText(configString)
    }

    fun saveConfig() {
        val configString = mTextEdit?.getText().toString()
        Utils.saveConfigsToStorage(this, configString)
    }

    fun resetConfig() {
        Utils.saveConfigsToStorage(this, "{}")
    }
}