package com.example.xiaomingassistant.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import com.example.xiaomingassistant.R
import com.example.xiaomingassistant.ui.view.RealtimeBlurView.RealtimeBlurView

class TopBarWithScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private lateinit var topBarContainer: View
    private lateinit var blurView: RealtimeBlurView
    private lateinit var floatingTitle: TextView
    private lateinit var scrollView: NestedScrollView
    private lateinit var contentContainer: LinearLayout
    private lateinit var contentHost: LinearLayout
    private lateinit var titleSpacer: View

    private var currentTopBarHeight = 0
    private var statusBarHeight = 0

    private var startTitleX = 0f
    private var startTitleY = 0f
    private var endTitleX = 0f
    private var endTitleY = 0f
    private var collapseRange = 1f
    private var collapsedScale = 0.56f

    private var initialTitleText: String? = null
    private var isSnapping = false

    init {
        LayoutInflater.from(context).inflate(
            R.layout.myview_topbar_with_scrollview,
            this,
            true
        )

        clipChildren = false
        clipToPadding = false

        val ta = context.obtainStyledAttributes(
            attrs,
            R.styleable.TopBarWithScrollView,
            defStyleAttr,
            0
        )
        initialTitleText = ta.getString(R.styleable.TopBarWithScrollView_titleText)
        ta.recycle()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        bindViews()
        moveExternalChildrenToContentHost()
        setupInsets()
        setupInitialLayout()
        setupScroll()
        setupSnap()

        ViewCompat.requestApplyInsets(this)
    }

    private fun bindViews() {
        topBarContainer = findViewById(R.id.top_bar_container)
        blurView = findViewById(R.id.top_bar_blur_bg)
        floatingTitle = findViewById(R.id.floating_title)
        scrollView = findViewById(R.id.myview_scrollview)
        contentContainer = findViewById(R.id.myview_content_container)
        contentHost = findViewById(R.id.content_host)
        titleSpacer = findViewById(R.id.title_spacer)

        initialTitleText?.let {
            floatingTitle.text = it
        }

        floatingTitle.isClickable = false
        floatingTitle.isFocusable = false
    }

    private fun moveExternalChildrenToContentHost() {
        val childrenToMove = mutableListOf<View>()

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.id != R.id.myview_root) {
                childrenToMove.add(child)
            }
        }

        childrenToMove.forEach { child ->
            removeView(child)
            contentHost.addView(child)
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets ->
            statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.navigationBars()).bottom

            currentTopBarHeight = statusBarHeight + dpToPx(60f)

            topBarContainer.updateLayoutParams {
                height = currentTopBarHeight
            }

            scrollView.clipToPadding = false
            contentContainer.updatePadding(
                top = currentTopBarHeight + dpToPx(20f),
                bottom = navBarHeight + dpToPx(80f)
            )

            setupInitialLayout()

            insets
        }
    }

    private fun setupInitialLayout() {
        post {
            val titleWidth = floatingTitle.width.toFloat()
            val titleHeight = floatingTitle.height.toFloat()
            val topBarContentHeight = dpToPx(60f).toFloat()

            // 缩放基准：左下角
            floatingTitle.pivotX = 0f
            floatingTitle.pivotY = titleHeight

            // 起点：顶栏下方左侧
            startTitleX = dpToPx(20f).toFloat()
            startTitleY = currentTopBarHeight + dpToPx(16f).toFloat()

            // 终点：飞到顶栏内容区正中
            val targetCenterX = width / 2f
            val targetCenterY = statusBarHeight + topBarContentHeight / 2f

            val collapsedWidth = titleWidth * collapsedScale
            val collapsedHeight = titleHeight * collapsedScale

            endTitleX = targetCenterX - collapsedWidth / 2f
            endTitleY = targetCenterY + collapsedHeight / 2f - titleHeight

            // 初始状态
            floatingTitle.translationX = startTitleX
            floatingTitle.translationY = startTitleY
            floatingTitle.scaleX = 1f
            floatingTitle.scaleY = 1f

            // 给滚动内容预留空间
            val spacerHeight = titleHeight.toInt() + dpToPx(0f)
            titleSpacer.updateLayoutParams {
                height = spacerHeight
            }
            collapseRange = spacerHeight.toFloat().coerceAtLeast(1f)

            // 如果当前已经有滚动，立即同步一次标题状态
            val progress = (scrollView.scrollY / collapseRange).coerceIn(0f, 1f)
            updateTitle(progress)
        }
    }

    private fun setupScroll() {
        scrollView.setOnScrollChangeListener { _: NestedScrollView, _: Int, scrollY: Int, _: Int, _: Int ->
            val progress = (scrollY / collapseRange).coerceIn(0f, 1f)
            updateTitle(progress)
            updateBlur(progress)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSnap() {
        scrollView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
                scrollView.postDelayed({
                    if (isSnapping) return@postDelayed

                    val scrollY = scrollView.scrollY
                    val progress = (scrollY / collapseRange).coerceIn(0f, 1f)

                    // 只在中间态时吸附
                    if (progress in 0.05f..0.95f) {
                        isSnapping = true
                        if (progress < 0.5f) {
                            scrollView.smoothScrollTo(0, 0)
                        } else {
                            scrollView.smoothScrollTo(0, collapseRange.toInt())
                        }

                        scrollView.postDelayed({
                            isSnapping = false
                        }, 300)
                    }
                }, 80)
            }
            false
        }
    }

    private fun updateTitle(progress: Float) {
        floatingTitle.translationX = lerp(startTitleX, endTitleX, progress)
        floatingTitle.translationY = lerp(startTitleY, endTitleY, progress)

        val scale = lerp(1f, collapsedScale, progress)
        floatingTitle.scaleX = scale
        floatingTitle.scaleY = scale
    }

    private fun updateBlur(progress: Float) {
        val blurRadius = resources.getDimension(R.dimen.global_blur_radius) * progress
        blurView.setBlurRadius(blurRadius)
        val alpha = (progress * 153).toInt()
        blurView.setOverlayColor(Color.argb(alpha, 255, 255, 255))
    }

    fun setTitle(text: CharSequence) {
        floatingTitle.text = text
        setupInitialLayout()
    }

    private fun lerp(start: Float, end: Float, progress: Float): Float {
        return start + (end - start) * progress
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }
}