package gr.escsoft.michaelprimez.searchablespinner;

import android.animation.Animator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.Px;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.joanzapata.iconify.Iconify;
import com.joanzapata.iconify.fonts.MaterialModule;
import com.joanzapata.iconify.widget.IconTextView;

import gr.escsoft.michaelprimez.searchablespinner.interfaces.OnItemSelectedListener;
import gr.escsoft.michaelprimez.searchablespinner.tools.EditCursorColor;
import gr.escsoft.michaelprimez.searchablespinner.tools.UITools;

/**
 * Created by michael on 1/8/17.
 */

public class SearchableSpinner extends RelativeLayout implements View.OnClickListener {

    private static final int DefaultElevation = 16;
    private static final int DefaultAnimationDuration = 400;
    private ViewState mViewState = ViewState.ShowingRelealedLayout;

    private CardView mRevealContainerCardView;
    private LinearLayout mRevealItem;
    private IconTextView mStartSearchImageView;

    private CardView mContainerCardView;
    private AppCompatEditText mSearchEditText;
    private IconTextView mDoneSearchImageView;
    private LinearLayout mSpinnerListContainer;
    private PopupWindow mPopupWindow;
    private ListView mSpinnerListView;
    private TextView mEmptyTextView;

    private Context mContext;
    private OnItemSelectedListener mOnItemSelected;
    private SelectedView mCurrSelectedView;
    private int mScreenHeightPixels;
    private int mScreenWidthPixels;

    /* Attributes */
    private @ColorInt int mRevealViewBackgroundColor;
    private @ColorInt int mStartEditTintColor;
    private @ColorInt int mEditViewBackgroundColor;
    private @ColorInt int mEditViewTextColor;
    private @ColorInt int mDoneEditTintColor;
    private @Px int mBordersSize;
    private @Px int mExpandSize;
    private boolean mShowBorders;
    private @ColorInt int mBoarderColor;
    private boolean mKeepLastSearch;
    private String mRevealEmptyText;
    private String mSearchHintText;
    private String mNoItemsFoundText;
    private int mAnimDuration;

    public enum ViewState {
        ShowingRelealedLayout,
        ShowingEditLayout,
        ShowingAnimation
    }

    static {
        Iconify.with(new MaterialModule());
    }

    public SearchableSpinner(@NonNull Context context) {
        this(context, null);
    }

    public SearchableSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SearchableSpinner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SearchableSpinner(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        getAttributeSet(attrs, defStyleAttr, defStyleRes);

        final LayoutInflater factory = LayoutInflater.from(context);
        factory.inflate(R.layout.view_searchable_spinner, this, true);

        mSpinnerListContainer = (LinearLayout) factory.inflate(R.layout.view_list, this, false);
        mSpinnerListView = (ListView) mSpinnerListContainer.findViewById(R.id.LstVw_SpinnerListView);
        mEmptyTextView = (TextView) mSpinnerListContainer.findViewById(R.id.TxtVw_EmptyText);
        mSpinnerListView.setEmptyView(mEmptyTextView);
    }

    private void getAttributeSet(@Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        if (attrs != null) {
            TypedArray attributes = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.SearchableSpinner, defStyleAttr, defStyleRes);
            mRevealViewBackgroundColor = attributes.getColor(R.styleable.SearchableSpinner_RevealViewBackgroundColor, Color.WHITE);
            mStartEditTintColor = attributes.getColor(R.styleable.SearchableSpinner_StartSearchTintColor, Color.GRAY);
            mEditViewBackgroundColor = attributes.getColor(R.styleable.SearchableSpinner_SearchViewBackgroundColor, Color.WHITE);
            mEditViewTextColor = attributes.getColor(R.styleable.SearchableSpinner_SearchViewTextColor, Color.BLACK);
            mDoneEditTintColor = attributes.getColor(R.styleable.SearchableSpinner_DoneSearchTintColor, Color.GRAY);
            mBordersSize = attributes.getDimensionPixelSize(R.styleable.SearchableSpinner_BordersSize, UITools.dpToPx(mContext, 4));
            mExpandSize = attributes.getDimensionPixelSize(R.styleable.SearchableSpinner_SpinnerExpandHeight, 0);
            mShowBorders = attributes.getBoolean(R.styleable.SearchableSpinner_ShowBorders, false);
            mBoarderColor = attributes.getColor(R.styleable.SearchableSpinner_BoarderColor, Color.GRAY);
            mAnimDuration = attributes.getColor(R.styleable.SearchableSpinner_AnimDuration, DefaultAnimationDuration);
            mKeepLastSearch = attributes.getBoolean(R.styleable.SearchableSpinner_KeepLastSearch, false);
            mRevealEmptyText = attributes.getString(R.styleable.SearchableSpinner_RevealEmptyText);
            mSearchHintText = attributes.getString(R.styleable.SearchableSpinner_SearchHintText);
            mNoItemsFoundText = attributes.getString(R.styleable.SearchableSpinner_NoItemsFoundText);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRevealContainerCardView = (CardView) findViewById(R.id.CrdVw_RevealContainer);
        mRevealContainerCardView.setOnClickListener(mOnRevelViewClickListener);
        mRevealItem = (LinearLayout) findViewById(R.id.FrmLt_SelectedItem);
        mStartSearchImageView = (IconTextView) findViewById(R.id.ImgVw_StartSearch);

        mContainerCardView = (CardView) findViewById(R.id.CrdVw_Container);
        mSearchEditText = (AppCompatEditText) findViewById(R.id.EdtTxt_SearchEditText);
        mDoneSearchImageView = (IconTextView) findViewById(R.id.ImgVw_DoneSearch);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        getScreenSize();
        mPopupWindow.setWidth(View.MeasureSpec.getSize(widthMeasureSpec));
        mPopupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (mExpandSize <= 0) {
            mPopupWindow.setHeight(mScreenHeightPixels - (t + b));
        }
        super.onLayout(changed, l, t, r, b);
    }

    private void init() {
        setupColors();
        setupList();
        mStartSearchImageView.setOnClickListener(this);
        mDoneSearchImageView.setOnClickListener(this);
        mSearchEditText.addTextChangedListener(mTextWatcher);


        mPopupWindow = new PopupWindow(mContext);
        mPopupWindow.setContentView(mSpinnerListContainer);
        mPopupWindow.setOutsideTouchable(false);
        mPopupWindow.setFocusable(false);
        mPopupWindow.setElevation(DefaultElevation);
        mPopupWindow.setBackgroundDrawable(ContextCompat.getDrawable(mContext, R.drawable.spinner_drawable));

        mSpinnerListView.setOnItemClickListener(mOnItemSelectedListener);
        if (!TextUtils.isEmpty(mSearchHintText)) {
            mSearchEditText.setHint(mSearchHintText);
        }
        if (!TextUtils.isEmpty(mNoItemsFoundText)) {
            mEmptyTextView.setText(mNoItemsFoundText);
        }
        if (mCurrSelectedView == null && !TextUtils.isEmpty(mRevealEmptyText)) {
            TextView textView = new TextView(mContext);
            textView.setText(mRevealEmptyText);
            mCurrSelectedView = new SelectedView(textView, -1, 0);
            mRevealItem.addView(textView);
        }
        clearAnimation();
        clearFocus();
    }

    private AdapterView.OnItemClickListener mOnItemSelectedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mCurrSelectedView == null) {
                mCurrSelectedView = new SelectedView(view, position, id);
            } else {
                mCurrSelectedView.setView(view);
                mCurrSelectedView.setPosition(position);
                mCurrSelectedView.setId(id);
            }
            if (mCurrSelectedView == null) {
                if (mOnItemSelected != null)
                    mOnItemSelected.onNothingSelected();
            } else if (mCurrSelectedView != null) {
                mRevealItem.removeAllViews();
                mSpinnerListView.removeViewInLayout(mCurrSelectedView.getView());
                mRevealItem.addView(mCurrSelectedView.getView());
                ((BaseAdapter) mSpinnerListView.getAdapter()).notifyDataSetChanged();
                if (mOnItemSelected != null)
                    mOnItemSelected.onItemSelected(mCurrSelectedView.getView(), mCurrSelectedView.getPosition(), mCurrSelectedView.getId());
            }
            hideEdit();
        }
    };

    private OnClickListener mOnRevelViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mViewState == ViewState.ShowingRelealedLayout) {
                revealEditView();
            } else if (mViewState == ViewState.ShowingEditLayout) {
                hideEditView();
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mViewState == ViewState.ShowingEditLayout) {
            hideEditView();
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            ((Filterable) mSpinnerListView.getAdapter()).getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void setupColors() {
        mRevealContainerCardView.setBackgroundColor(mRevealViewBackgroundColor);
        mRevealItem.setBackgroundColor(mRevealViewBackgroundColor);
        mStartSearchImageView.setBackgroundColor(mRevealViewBackgroundColor);
        mStartSearchImageView.setTextColor(mStartEditTintColor);

        mContainerCardView.setBackgroundColor(mEditViewBackgroundColor);
        mSearchEditText.setBackgroundColor(mEditViewBackgroundColor);
        mSearchEditText.setTextColor(mEditViewTextColor);
        mSearchEditText.setHintTextColor(mStartEditTintColor);
        EditCursorColor.setCursorColor(mSearchEditText, mEditViewTextColor);
        mDoneSearchImageView.setBackgroundColor(mEditViewBackgroundColor);
        mDoneSearchImageView.setTextColor(mDoneEditTintColor);
    }

    private void setupList() {
        ViewGroup.MarginLayoutParams spinnerListViewLayoutParams = (ViewGroup.MarginLayoutParams) mSpinnerListView.getLayoutParams();
        ViewGroup.MarginLayoutParams emptyTextViewLayoutParams = (ViewGroup.MarginLayoutParams) mEmptyTextView.getLayoutParams();
        ViewGroup.LayoutParams spinnerListContainerLayoutParams = mSpinnerListContainer.getLayoutParams();
        LinearLayout.LayoutParams listLayoutParams = (LinearLayout.LayoutParams) mSpinnerListView.getLayoutParams();

        spinnerListContainerLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        if (mExpandSize <= 0) {
            listLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        } else {
            listLayoutParams.height = mExpandSize;
        }
        mSpinnerListContainer.setBackgroundColor(mBoarderColor);
        if (mShowBorders) {
            spinnerListViewLayoutParams.setMargins(mBordersSize, mBordersSize, mBordersSize, mBordersSize);
        } else {
            spinnerListViewLayoutParams.setMargins(0, 0, 0, 0);
        }
    }

    public void setAdapter(ListAdapter adapter) {
        if (!(adapter instanceof Filterable))
            throw new IllegalArgumentException("Adapter should implement the Filterable interface");
        mSpinnerListView.setAdapter(adapter);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        mOnItemSelected = onItemSelectedListener;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getScreenSize();
    }

    private void getScreenSize() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenHeightPixels = metrics.heightPixels;
        mScreenWidthPixels = metrics.widthPixels;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.ImgVw_StartSearch) {
            revealEdit();
        } else if (id == R.id.ImgVw_DoneSearch) {
            hideEdit();
        }
    }

    public void revealEdit() {
        if (mViewState == ViewState.ShowingRelealedLayout) {
            if (!mKeepLastSearch)
                mSearchEditText.setText(null);
            revealEditView();
        }
    }

    public void hideEdit() {
        if (mViewState == ViewState.ShowingEditLayout) {
            hideEditView();
        }
    }

    private void revealEditView() {
        mViewState = ViewState.ShowingAnimation;

        final int cx = mRevealContainerCardView.getLeft();
        final int cxr = mRevealContainerCardView.getRight();
        final int cy = (mRevealContainerCardView.getTop() + mRevealContainerCardView.getHeight())/2;
        final int reverse_startradius = Math.max(mRevealContainerCardView.getWidth(), mRevealContainerCardView.getHeight());
        final int reverse_endradius = 0;

        mPopupWindow.showAsDropDown(this, cx, 0);

        final Animator revealAnimator = ViewAnimationUtils.createCircularReveal(mRevealContainerCardView, cx, cy, reverse_startradius, reverse_endradius);
        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewState = ViewState.ShowingEditLayout;
                mRevealContainerCardView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final Animator animator = ViewAnimationUtils.createCircularReveal(mContainerCardView, cxr, cy, reverse_endradius, reverse_startradius);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mContainerCardView.setVisibility(View.VISIBLE);
                mViewState = ViewState.ShowingEditLayout;
                //((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mSpinnerListContainer.setVisibility(View.VISIBLE);
        mContainerCardView.setVisibility(View.VISIBLE);
        animator.setDuration(mAnimDuration);
        revealAnimator.setDuration(mAnimDuration);


        animator.start();
        revealAnimator.start();
        mPopupWindow.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPopupWindow.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final Animator spinnerListContainerAnimator = ViewAnimationUtils.createCircularReveal(mPopupWindow.getContentView(), cxr, cy, reverse_endradius, reverse_startradius);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSpinnerListContainer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                spinnerListContainerAnimator.setDuration(mAnimDuration);
                spinnerListContainerAnimator.start();
            }
        });

    }

    private void hideEditView() {
        mViewState = ViewState.ShowingAnimation;
        final int cx = mContainerCardView.getLeft();
        final int cxr = mContainerCardView.getRight();
        final int cy = (mContainerCardView.getTop() + mContainerCardView.getHeight())/2;
        final int reverse_startradius = Math.max(mContainerCardView.getWidth(), mContainerCardView.getHeight());
        final int reverse_endradius = 0;

        final Animator revealAnimator = ViewAnimationUtils.createCircularReveal(mRevealContainerCardView, cx, cy, reverse_endradius, reverse_startradius);
        revealAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewState = ViewState.ShowingRelealedLayout;
                mRevealContainerCardView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        final Animator animator = ViewAnimationUtils.createCircularReveal(mContainerCardView, cxr, cy, reverse_startradius, reverse_endradius);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mContainerCardView.setVisibility(View.INVISIBLE);
                mViewState = ViewState.ShowingRelealedLayout;
                mPopupWindow.dismiss();
                ((InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mSearchEditText.getWindowToken(), 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mRevealContainerCardView.setVisibility(View.VISIBLE);
        animator.setDuration(mAnimDuration);
        animator.start();

        revealAnimator.setDuration(mAnimDuration);
        revealAnimator.start();


        mPopupWindow.getContentView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mPopupWindow.getContentView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                final Animator spinnerListContainerAnimator = ViewAnimationUtils.createCircularReveal(mPopupWindow.getContentView(), cxr, cy, reverse_startradius, reverse_endradius);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSpinnerListContainer.setVisibility(View.GONE);
                        mPopupWindow.dismiss();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                spinnerListContainerAnimator.setDuration(mAnimDuration);
                spinnerListContainerAnimator.start();
            }
        });
    }
}
