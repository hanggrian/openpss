package androidx.appcompat.widget

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.InputType
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.ViewCompat
import com.hendraanggrian.openpss.R

class SearchBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.searchViewStyle
) : SearchView(context, attrs, defStyleAttr) {

    private val mSearchEditFrame = findViewById<View>(androidx.appcompat.R.id.search_edit_frame)
    private val mSearchPlate = findViewById<View>(androidx.appcompat.R.id.search_plate)
    private val mSubmitArea = findViewById<View>(androidx.appcompat.R.id.submit_area)

    val input: EditText get() = mSearchSrcTextView

    init {
        // Set up icons and backgrounds.
        val transparent = ColorDrawable(resources.getColor(android.R.color.transparent))
        ViewCompat.setBackground(mSearchPlate, transparent)
        ViewCompat.setBackground(mSubmitArea, transparent)

        mSearchSrcTextView.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        // Buttons are wider in Google Search app.
        mCloseButton.scaleType = ImageView.ScaleType.CENTER
        mCloseButton.layoutParams.width = getDimenAttr(context, android.R.attr.actionBarSize)
    }

    private fun getColorAttr(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        return typedValue.data
    }

    private fun getDimenAttr(context: Context, attrId: Int): Int {
        val typedValue = TypedValue()
        context.theme.resolveAttribute(attrId, typedValue, true)
        val textSizeAttr = intArrayOf(attrId)
        val a = context.obtainStyledAttributes(typedValue.data, textSizeAttr)
        val value = a.getDimensionPixelSize(0, 0)
        a.recycle()
        return value
    }
}