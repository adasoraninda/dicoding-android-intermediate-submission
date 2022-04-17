package com.adasoraninda.dicodingstoryapp.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.core.widget.addTextChangedListener
import com.adasoraninda.dicodingstoryapp.R


class InputView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var isPasswordVisible = false
    private var isPasswordType = false

    private var bgInputDrawable: Drawable? = null
    private var passwordVisibleDrawable: Drawable? = null
    private var passwordInvisibleDrawable: Drawable? = null

    private val normalListener: TextWatcher = addTextChangedListener {
        val text = it.toString()

        if (text.isEmpty()) {
            error = context.getString(R.string.error_input_empty)
            return@addTextChangedListener
        }

        error = null
    }

    private val emailListener: TextWatcher = addTextChangedListener {
        val text = it.toString()

        if (text.isEmpty()) {
            error = context.getString(R.string.error_input_empty_email)
            return@addTextChangedListener
        }

        if (PatternsCompat.EMAIL_ADDRESS.matcher(text).matches().not()) {
            error = context.getString(R.string.error_input_match_email)
            return@addTextChangedListener
        }

        error = null
    }

    private val passwordListener: TextWatcher = addTextChangedListener {
        val text = it.toString()

        if (text.isEmpty()) {
            error = context.getString(R.string.error_input_empty_password)
            return@addTextChangedListener
        }

        if (text.length < 6) {
            error = context.getString(R.string.error_input_match_password)
            return@addTextChangedListener
        }

        error = null
    }

    init {
        setup()
        checkInputType()
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = super.onSaveInstanceState()
        val bundle = Bundle()
        bundle.putParcelable(SUPER_KEY, state)
        bundle.putBoolean(PASS_VISIBILITY_KEY, isPasswordVisible)
        bundle.putString(ERROR_MESSAGE_KEY, error?.toString())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var newState = state
        var errorMessage: String? = null

        if (newState is Bundle) {
            if (isPasswordType) isPasswordVisible = newState.getBoolean(PASS_VISIBILITY_KEY)
            errorMessage = newState.getString(ERROR_MESSAGE_KEY)
            newState = newState.getParcelable(SUPER_KEY)
        }
        super.onRestoreInstanceState(newState)

        if (isPasswordType) if (isPasswordVisible) showPassword() else hidePassword()
        error = errorMessage
    }

    override fun onDetachedFromWindow() {
        removeTextChangedListener(emailListener)
        removeTextChangedListener(normalListener)
        removeTextChangedListener(passwordListener)
        super.onDetachedFromWindow()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        background = bgInputDrawable
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    override fun onTouch(view: View?, me: MotionEvent?): Boolean {
        if (compoundDrawables[2] == null) return false

        val event = me ?: return false
        val buttonWidth = passwordVisibleDrawable?.intrinsicWidth ?: 0

        val isPasswordButtonClicked = if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
            val buttonPosition = (width + paddingStart).toFloat()
            event.x < buttonPosition
        } else {
            val buttonPosition = (width - paddingEnd - buttonWidth).toFloat()
            event.x > buttonPosition
        }

        if (!isPasswordButtonClicked) return false

        return when (event.action) {
            MotionEvent.ACTION_UP -> {
                isPasswordVisible = !isPasswordVisible
                if (isPasswordVisible) {
                    showPassword()
                } else {
                    hidePassword()
                }
                setSelection(length())
                true
            }
            else -> false
        }
    }

    private fun setup() {
        val color = ContextCompat.getColor(context, R.color.teal_200)

        bgInputDrawable = ContextCompat.getDrawable(context, R.drawable.bg_input) as Drawable
        passwordVisibleDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_visibility) as Drawable
        passwordInvisibleDrawable =
            ContextCompat.getDrawable(context, R.drawable.ic_visibility_off) as Drawable

        passwordInvisibleDrawable?.setTint(color)
        passwordVisibleDrawable?.setTint(color)
    }

    private fun showPassword() {
        inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_CLASS_TEXT
        setInputDrawables(passwordInvisibleDrawable)
    }

    private fun hidePassword() {
        inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT
        setInputDrawables(passwordVisibleDrawable)
    }

    private fun checkInputType() {

        when (inputType) {
            InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS or InputType.TYPE_CLASS_TEXT -> {
                addTextChangedListener(emailListener)
            }
            InputType.TYPE_TEXT_VARIATION_PASSWORD or InputType.TYPE_CLASS_TEXT -> {
                isPasswordType = true
                setInputDrawables(passwordVisibleDrawable)
                addTextChangedListener(passwordListener)
                setOnTouchListener(this)
            }
            else -> addTextChangedListener(normalListener)
        }
    }

    private fun setInputDrawables(drawable: Drawable? = null) {
        setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
        setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            drawable,
            null
        )
    }

    companion object {
        private const val PASS_VISIBILITY_KEY = "password"
        private const val SUPER_KEY = "super"
        private const val ERROR_MESSAGE_KEY = "error"
    }
}