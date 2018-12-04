package ysn.com.timeview.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import ysn.com.timeview.R;
import ysn.com.timeview.bean.Time;
import ysn.com.timeview.util.ResUtil;
import ysn.com.timeview.util.UiUtil;

/**
 * @Author yangsanning
 * @ClassName TimeView
 * @Description 一句话概括作用
 * @Date 2018/12/4
 * @History 2018/12/4 author: description:
 */
public class TimeView extends View {

    private static final String BK = "BK";
    private static final String SH = "sh";
    private static final String SZ = "sz";
    private static final int COUNT_DEFAULT = 240;
    private static final String[] TIME_TEXT = new String[]{"09:30", "11:30/13:00", "15:00"};

    /**
     * 默认虚线效果
     */
    private static final PathEffect DEFAULT_DASH_EFFECT = new DashPathEffect(new float[]{2, 2, 2, 2}, 1);

    private static final float DEFAULT_PRICE_STROKE_WIDTH = 2.5f;

    /**
     * 两边边距
     */
    private float tableMargin = 1;

    /**
     * 坐标字体大小
     */
    private float xYTextSize = 25;

    /**
     * 坐标字体边距
     */
    private float xYTextMargin = xYTextSize / 5;

    /**
     * columnCount: 列数+1
     * topRowCount: 上表横数+1
     * bottomRowCount: 下表横数+1
     */
    private int columnCount = 4;
    private int topRowCount = 4;
    private int bottomRowCount = 2;

    /**
     * 虚线效果
     */
    private PathEffect mDashEffect = DEFAULT_DASH_EFFECT;

    /**
     * 当前价格集合
     */
    private List<Float> stockPriceList = new ArrayList<>();
    private List<Float> stockAvePriceList = new ArrayList<>();
    private List<String> timeList = new ArrayList<>();
    private Paint xYTextPaint;
    private float lastClose = 0.0f;
    private float maxStockPrice = 0.0f;
    private float minStockPrice = 0.0f;
    private String percent = " 100%";

    private DecimalFormat decimalFormat;

    private Paint dottedLinePaint;
    private Path linePath;

    private float viewHeight;
    private float topTableHeight;
    private float titleTableHeight;
    private float timeTableHeight;
    private float bottomTableHeight;
    private int viewWidth;
    private float xSpace;
    private float ySpace;

    private Paint linePaint;
    private Path pricePath;
    private Paint pricePaint;
    private Path avePricePath;
    private Paint avePricePaint;
    private Path priceAreaPath;
    private Paint priceAreaPaint;

    private Rect textRect = new Rect();
    private String tempText;
    private boolean isOtherCode;
    String avePrice, trade, zf, changepercent;

    /**
     * 当前交易量
     */
    private List<Float> stockVolumeList = new ArrayList<>();
    private float maxStockVolume;
    private String maxStockVolumeString = "", centreStockVolumeString = "";
    private int volume;

    /**
     * HEART_RADIUS: 心脏半径
     * HEART_DIAMETER: 心脏直径
     * HEART_INIT_ALPHA: 初始透明度
     * HEART_BEAT_RATE: 心跳率
     * HEART_BEAT_FRACTION_RATE: 心跳动画时间
     */
    private static final float HEART_RADIUS = 5f;
    private static final float HEART_DIAMETER = 40f;
    private static final int HEART_INIT_ALPHA = 255;
    private static final long HEART_BEAT_RATE = 2000;
    private static final long HEART_BEAT_FRACTION_RATE = 2000;

    /**
     * isBeat: 是否跳动
     * beatFraction: 变化率
     */
    private boolean isBeat = false;
    private float beatFraction;
    private Paint heartPaint;
    private ValueAnimator beatAnimator;
    private Handler beatHandler = new Handler();
    private Runnable beatRunnable = new Runnable() {
        @Override
        public void run() {
            beatAnimator.start();
            invalidate();
            beatHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    private Paint slipPaint;
    private Paint slipAreaPaint;
    private Path path;
    private boolean isShowDetails;
    private float touchX, touchY;
    private int touchNum = 0;

    /**
     * 滑动坐标
     */
    private float slipPriceLeft;
    private float slipPriceTop;
    private float slipPriceRight;
    private float slipPriceBottom;
    private float slipPrice;

    public TimeView(Context context) {
        this(context, null);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        linePaint = new Paint();
        linePaint.setColor(ResUtil.getColor(R.color.stock_line));
        linePaint.setStrokeWidth(1f);
        linePaint.setStyle(Paint.Style.STROKE);

        dottedLinePaint = new Paint();
        dottedLinePaint.setStyle(Paint.Style.STROKE);
        dottedLinePaint.setPathEffect(mDashEffect);
        dottedLinePaint.setStrokeWidth(1f);

        linePath = new Path();

        xYTextPaint = new Paint();
        xYTextPaint.setAntiAlias(true);
        xYTextPaint.setStyle(Paint.Style.STROKE);
        decimalFormat = new DecimalFormat("0.00");

        pricePath = new Path();
        pricePaint = new Paint();
        pricePaint.setColor(ResUtil.getColor(R.color.stock_price_line));
        pricePaint.setAntiAlias(true);
        pricePaint.setStyle(Paint.Style.STROKE);
        pricePaint.setStrokeWidth(DEFAULT_PRICE_STROKE_WIDTH);

        avePricePath = new Path();
        avePricePaint = new Paint();
        avePricePaint.setColor(ResUtil.getColor(R.color.stock_ave_price_line));
        avePricePaint.setAntiAlias(true);
        avePricePaint.setStyle(Paint.Style.STROKE);
        avePricePaint.setStrokeWidth(DEFAULT_PRICE_STROKE_WIDTH);

        priceAreaPath = new Path();
        priceAreaPaint = new Paint();
        priceAreaPaint.setColor(ResUtil.getColor(R.color.stock_price_line));
        priceAreaPaint.setStyle(Paint.Style.FILL);
        priceAreaPaint.setStrokeWidth(2);
        priceAreaPaint.setAlpha(15);

        heartPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        heartPaint.setAntiAlias(true);
        beatAnimator = ValueAnimator.ofFloat(0, 1f).setDuration(HEART_BEAT_FRACTION_RATE);
        beatAnimator.addUpdateListener(animation -> {
            beatFraction = (float) animation.getAnimatedValue();
            invalidate();
        });

        slipPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slipPaint.setColor(ResUtil.getColor(R.color.stock_slip_line));
        slipPaint.setStrokeWidth(2.0f);
        slipPaint.setStyle(Paint.Style.STROKE);

        path = new Path();
        slipAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        slipAreaPaint.setColor(ResUtil.getColor(R.color.stock_area_fq));
        slipAreaPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        viewHeight = getHeight();
        titleTableHeight = viewHeight * 0.055f;
        timeTableHeight = viewHeight * 0.055f;
        xYTextSize = titleTableHeight * 0.8f;
        xYTextMargin = (titleTableHeight - xYTextSize) / 2;
        topTableHeight = viewHeight * 0.67f - titleTableHeight;
        bottomTableHeight = viewHeight - titleTableHeight - topTableHeight - timeTableHeight - 1;
        viewWidth = getWidth();

        xSpace = (viewWidth - tableMargin * 2) / (float) COUNT_DEFAULT;
        ySpace = (topTableHeight / (maxStockPrice - minStockPrice));

        xYTextPaint.setTextSize(xYTextSize);

        // 绘制边框
        drawBorders(canvas);
        // 绘制竖线
        drawColumnLine(canvas);
        // 绘制横线
        drawRowLine(canvas);
        // 绘制时间坐标
        drawTimeText(canvas);

        if (!stockPriceList.isEmpty()) {
            // 绘制头部信息
            drawTitleText(canvas);
            // 绘制价格、价格区域、均线、闪烁点
            drawPriceLine(canvas);
            // 绘制坐标峰值
            drawXYText(canvas);
            // 绘制量手,下表坐标
            drawValueHint(canvas);
            // 绘制柱形
            drawPillar(canvas);
            // 绘制滑动线
            drawSlipLine(canvas);
        }
    }

    /**
     * 绘制边框
     */
    private void drawBorders(Canvas canvas) {
        // 上表边框
        linePath.moveTo(tableMargin, titleTableHeight);
        linePath.lineTo(viewWidth - tableMargin, titleTableHeight);
        linePath.lineTo(viewWidth - tableMargin, topTableHeight + titleTableHeight);
        linePath.lineTo(tableMargin, topTableHeight + titleTableHeight);
        linePath.close();
        canvas.drawPath(linePath, linePaint);
        linePath.reset();

        // 下表边框
        linePath.moveTo(tableMargin, topTableHeight + titleTableHeight + timeTableHeight);
        linePath.lineTo(viewWidth - tableMargin, topTableHeight + titleTableHeight + timeTableHeight);
        linePath.lineTo(viewWidth - tableMargin, viewHeight - tableMargin);
        linePath.lineTo(tableMargin, viewHeight - tableMargin);
        linePath.close();
        canvas.drawPath(linePath, linePaint);
        linePath.reset();
    }

    /**
     * 绘制竖线
     */
    private void drawColumnLine(Canvas canvas) {
        // 绘制上表竖线
        dottedLinePaint.setColor(ResUtil.getColor(R.color.stock_dotted_column_line));
        float columnSpace = (viewWidth - 2 * tableMargin) / columnCount;
        for (int i = 1; i < columnCount; i++) {
            linePath.reset();
            linePath.moveTo(getX(columnSpace, i), titleTableHeight + 1);
            linePath.lineTo(getX(columnSpace, i), titleTableHeight + topTableHeight - 1);
            canvas.drawPath(linePath, dottedLinePaint);
        }

        // 绘制下表竖线
        for (int i = 1; i < columnCount; i++) {
            linePath.reset();
            linePath.moveTo(getX(columnSpace, i), viewHeight - bottomTableHeight - 1);
            linePath.lineTo(getX(columnSpace, i), viewHeight - -1);
            canvas.drawPath(linePath, dottedLinePaint);
        }
        linePath.reset();
    }

    /**
     * 绘制横线
     */
    private void drawRowLine(Canvas canvas) {
        // 绘制上表横线
        float rowSpacing = topTableHeight / topRowCount;
        for (int i = 1; i < topRowCount; i++) {
            linePath.reset();
            linePath.moveTo(tableMargin, topTableHeight + titleTableHeight - rowSpacing * i);
            linePath.lineTo(viewWidth - tableMargin, topTableHeight + titleTableHeight - rowSpacing * i);
            dottedLinePaint.setColor(ResUtil.getColor(i != topRowCount / 2 ?
                    R.color.stock_dotted_column_line : R.color.stock_dotted_row_line));
            canvas.drawPath(linePath, dottedLinePaint);
        }

        // 绘制下表横线
        dottedLinePaint.setColor(ResUtil.getColor(R.color.stock_dotted_column_line));
        rowSpacing = bottomTableHeight / bottomRowCount;
        for (int i = 1; i < bottomRowCount; i++) {
            linePath.reset();
            linePath.moveTo(tableMargin, (viewHeight - 1) - rowSpacing * i);
            linePath.lineTo(viewWidth - tableMargin, (viewHeight - 1) - rowSpacing * i);
            canvas.drawPath(linePath, dottedLinePaint);
        }
        linePath.reset();
    }


    /**
     * 绘制时间坐标
     */
    private void drawTimeText(Canvas canvas) {
        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_text_title));
        xYTextPaint.getTextBounds(TIME_TEXT[0], 0, TIME_TEXT[0].length(), textRect);
        canvas.drawText(TIME_TEXT[0], tableMargin,
                (viewHeight - bottomTableHeight) - timeTableHeight / 2 + textRect.height() / 2, xYTextPaint);

        xYTextPaint.getTextBounds(TIME_TEXT[1], 0, TIME_TEXT[1].length(), textRect);
        canvas.drawText(TIME_TEXT[1], (viewWidth - textRect.right) / 2 - tableMargin,
                (viewHeight - bottomTableHeight) - timeTableHeight / 2 + textRect.height() / 2, xYTextPaint);

        xYTextPaint.getTextBounds(TIME_TEXT[2], 0, TIME_TEXT[2].length(), textRect);
        canvas.drawText(TIME_TEXT[2], viewWidth - textRect.right - tableMargin,
                (viewHeight - bottomTableHeight) - timeTableHeight / 2 + textRect.height() / 2, xYTextPaint);
    }

    /**
     * 绘制价格、价格区域、均线、闪烁点
     */
    private void drawPriceLine(Canvas canvas) {
        float price = stockPriceList.get(0) - lastClose;
        pricePath.moveTo(tableMargin, getY(price));
        priceAreaPath.moveTo(tableMargin, topTableHeight + titleTableHeight);
        priceAreaPath.lineTo(tableMargin, getY(price));
        avePricePath.moveTo(tableMargin, getY(stockAvePriceList.get(0) - lastClose));
        for (int i = 1; i < stockPriceList.size(); i++) {
            price = stockPriceList.get(i) - lastClose;
            pricePath.lineTo(getX(i), getY(price));
            priceAreaPath.lineTo(getX(i), getY(price));
            avePricePath.lineTo(getX(i), getY(stockAvePriceList.get(i) - lastClose));

            if (isBeat && i == stockPriceList.size() - 1) {
                //绘制扩散圆
                heartPaint.setColor(ResUtil.getColor(R.color.stock_price_line));
                heartPaint.setAlpha((int) (HEART_INIT_ALPHA - HEART_INIT_ALPHA * beatFraction));
                canvas.drawCircle(getX(i), getY(price), (HEART_RADIUS + HEART_DIAMETER * beatFraction), heartPaint);
                // 绘制中心圆
                heartPaint.setAlpha(255);
                heartPaint.setColor(ResUtil.getColor(R.color.stock_price_line));
                canvas.drawCircle(getX(i), getY(price), HEART_RADIUS, heartPaint);
            }
        }
        priceAreaPath.lineTo(getX(stockPriceList.size() - 1), topTableHeight + titleTableHeight);
        priceAreaPath.close();

        canvas.drawPath(pricePath, pricePaint);
        canvas.drawPath(priceAreaPath, priceAreaPaint);
        canvas.drawPath(avePricePath, avePricePaint);
        pricePath.reset();
        priceAreaPath.reset();
        avePricePath.reset();
    }

    /**
     * 获取价格线的x轴坐标
     *
     * @param position 当前position
     * @return x轴坐标
     */
    private float getX(int position) {
        return getX(xSpace, position);
    }


    /**
     * 获取x轴坐标
     *
     * @param xSpace   x轴间隙
     * @param position 当前position
     * @return x轴坐标
     */
    private float getX(float xSpace, int position) {
        return tableMargin + xSpace * position;
    }

    /**
     * 获取y轴坐标
     *
     * @param price 当前价格
     * @return y轴坐标
     */
    private float getY(float price) {
        return topTableHeight / 2 - price * ySpace + titleTableHeight + 2;
    }

    /**
     * 绘制头部信息
     */
    private void drawTitleText(Canvas canvas) {

        if (isOtherCode) {
            initOtherCodeHead(isShowDetails ? touchNum : (stockPriceList.size() - 1));
        } else {
            initCodeHead(isShowDetails ? touchNum : stockPriceList.size() - 1);
        }

        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_ave_price_line));
        canvas.drawText(avePrice, tableMargin, 1 + xYTextSize, xYTextPaint);

        tempText = avePrice;
        xYTextPaint.getTextBounds(tempText, 0, tempText.length(), textRect);
        xYTextPaint.setColor(ResUtil.getColor(
                stockPriceList.get(isShowDetails ? touchNum : stockPriceList.size() - 1) >= lastClose ?
                        R.color.stock_red : R.color.stock_green));
        canvas.drawText(trade,
                tableMargin + xYTextMargin * 10 + textRect.width(),
                1 + xYTextSize, xYTextPaint);

        tempText += trade;
        xYTextPaint.getTextBounds(tempText, 0, tempText.length(), textRect);
        canvas.drawText(zf,
                tableMargin + xYTextMargin * 18 + textRect.width(),
                1 + xYTextSize, xYTextPaint);

        tempText += zf;
        xYTextPaint.getTextBounds(tempText, 0, tempText.length(), textRect);
        canvas.drawText(changepercent,
                tableMargin + xYTextMargin * 26 + textRect.width(),
                1 + xYTextSize, xYTextPaint);
    }

    private void initOtherCodeHead(int position) {
        avePrice = timeList.get(position);
        trade = "最新:" + decimalFormat.format(stockPriceList.get(position));
        zf = decimalFormat.format(stockPriceList.get(position) - lastClose);
        changepercent = decimalFormat.format(
                (stockPriceList.get(position) - lastClose) / lastClose * 100) + "%";
    }

    private void initCodeHead(int position) {
        avePrice = "均价:" + decimalFormat.format(stockAvePriceList.get(position));
        trade = "最新:" + decimalFormat.format(stockPriceList.get(position));
        zf = decimalFormat.format(stockPriceList.get(position) - lastClose);
        changepercent = decimalFormat.format(
                (stockPriceList.get(position) - lastClose) / lastClose * 100) + "%";
    }

    /**
     * 绘制坐标峰值
     */
    private void drawXYText(Canvas canvas) {
        // 价格最大值
        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_red));
        canvas.drawText(decimalFormat.format(maxStockPrice),
                getX(xYTextMargin, 2),
                titleTableHeight + xYTextSize, xYTextPaint);

        // 增幅
        xYTextPaint.getTextBounds("+" + percent, 0, ("+" + percent).length(), textRect);
        canvas.drawText("+" + percent,
                viewWidth - tableMargin - xYTextMargin * 2 - textRect.width(),
                titleTableHeight + xYTextSize, xYTextPaint);

        // 价格最小值
        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_green));
        canvas.drawText(decimalFormat.format(minStockPrice),
                getX(xYTextMargin, 2),
                titleTableHeight + topTableHeight - xYTextMargin * 2 - 2, xYTextPaint);

        // 减幅
        xYTextPaint.getTextBounds("-" + percent, 0, ("-" + percent).length(), textRect);
        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_green));
        canvas.drawText("-" + percent,
                viewWidth - tableMargin - xYTextMargin * 2 - textRect.width(),
                titleTableHeight + topTableHeight - xYTextMargin * 2 - 2, xYTextPaint);

        // 中间坐标
        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_text_title));
        canvas.drawText(decimalFormat.format(lastClose),
                getX(xYTextMargin, 2),
                (titleTableHeight + topTableHeight) / 2 + xYTextSize + 2, xYTextPaint);
    }

    /**
     * 绘制量手,下表坐标
     */
    private void drawValueHint(Canvas canvas) {
        String valueHint = "量：";
        String value;
        if (isOtherCode) {
            value = UiUtil.getDecimalZero(stockVolumeList.get(stockVolumeList.size() - 1));
        } else {
            value = UiUtil.getDecimalZero(stockVolumeList.get(stockVolumeList.size() - 1) / 100);
        }

        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_text_title));
        canvas.drawText(valueHint, getX(xYTextMargin, 2),
                viewHeight - bottomTableHeight + xYTextSize + xYTextMargin, xYTextPaint);

        tempText = valueHint;
        xYTextPaint.getTextBounds(tempText, 0, tempText.length(), textRect);
        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_green));
        canvas.drawText(value, tableMargin + xYTextMargin * 2 + textRect.width(),
                viewHeight - bottomTableHeight + xYTextSize + xYTextMargin, xYTextPaint);


        if (volume != 0) {
            String handHint = "现手：";
            String hand = UiUtil.getVolume(volume);
            tempText += value;
            xYTextPaint.getTextBounds(tempText, 0, tempText.length(), textRect);
            xYTextPaint.setColor(ResUtil.getColor(R.color.stock_text_title));
            canvas.drawText(handHint, tableMargin + xYTextMargin * 8 + textRect.width(),
                    viewHeight - bottomTableHeight + xYTextSize + xYTextMargin, xYTextPaint);

            tempText += handHint;
            xYTextPaint.getTextBounds(tempText, 0, tempText.length(), textRect);
            xYTextPaint.setColor(ResUtil.getColor(R.color.stock_green));
            canvas.drawText(hand, tableMargin + xYTextMargin * 8 + textRect.width(),
                    viewHeight - bottomTableHeight + xYTextSize + xYTextMargin, xYTextPaint);
        }

        // 下表格最大量
        xYTextPaint.getTextBounds(maxStockVolumeString, 0, maxStockVolumeString.length(), textRect);
        canvas.drawText(maxStockVolumeString,
                viewWidth - tableMargin - xYTextMargin * 2 - textRect.width(),
                titleTableHeight + topTableHeight + timeTableHeight + textRect.height() + xYTextMargin * 2 + 2, xYTextPaint);

        // 下表格中间值
        xYTextPaint.getTextBounds(centreStockVolumeString, 0, centreStockVolumeString.length(), textRect);
        canvas.drawText(centreStockVolumeString,
                viewWidth - tableMargin - xYTextMargin * 2 - textRect.width(),
                titleTableHeight + topTableHeight + timeTableHeight + (bottomTableHeight + textRect.height()) / 2 - tableMargin,
                xYTextPaint);
    }

    /**
     * 绘制柱形
     * columnSpace: 宽 - 边距 - 柱子间距(1f)
     */
    private void drawPillar(Canvas canvas) {
        float columnSpace = (viewWidth - (tableMargin * 2) - (COUNT_DEFAULT * 1f)) / COUNT_DEFAULT;
        Paint paint = new Paint();
        paint.setStrokeWidth(columnSpace);
        for (int i = 0; i < stockPriceList.size() - 1; i++) {
            if (i == 0) {
                paint.setColor(ResUtil.getColor(
                        stockPriceList.get(i) >= lastClose ? R.color.stock_red : R.color.stock_green));
            } else {
                paint.setColor(ResUtil.getColor(
                        stockPriceList.get(i) >= stockPriceList.get(i - 1) ? R.color.stock_red : R.color.stock_green));
            }
            canvas.drawLine(tableMargin + (columnSpace * i) + (i * 1f) + 1,
                    viewHeight - 1,
                    tableMargin + (columnSpace * i) + (i * 1f) + 1,
                    viewHeight - (stockVolumeList.get(i) / maxStockVolume) * (bottomTableHeight - titleTableHeight) * 0.95f, paint);
        }
        paint.reset();
    }

    /**
     * 绘制滑动线
     */
    private void drawSlipLine(Canvas canvas) {

        if (touchX - tableMargin > 0 && touchX < tableMargin + viewWidth) {
            touchNum = (int) ((touchX - tableMargin) / viewWidth * COUNT_DEFAULT);
        } else if (touchX - tableMargin - 2 < 0) {
            touchNum = 0;
        } else if (touchX > tableMargin + 1 + viewWidth) {
            touchNum = stockPriceList.size() - 1;
        }

        if (touchNum >= stockPriceList.size()) {
            touchNum = stockPriceList.size() - 1;
        }

        if (isShowDetails) {
            // 竖线
            canvas.drawLine(touchX >= viewWidth ? viewWidth - tableMargin : getX(touchNum), titleTableHeight,
                    touchX >= viewWidth ? viewWidth - tableMargin : getX(touchNum), viewHeight, slipPaint);

            // 画时间
            drawSlipTime(canvas);

            if (touchY <= titleTableHeight + topTableHeight) {
                // 绘制滑动价格值
                drawSlipPrice(canvas);
            } else {
                // 绘制滑动成交量值
                drawSlipVolume(canvas);
            }
        }
    }


    /**
     * 画时间
     */
    private void drawSlipTime(Canvas canvas) {
        xYTextPaint.setColor(ResUtil.getColor(R.color.stock_text_title));
        xYTextPaint.getTextBounds(timeList.get(touchNum),
                0, timeList.get(touchNum).length(), textRect);

        slipPriceLeft = getX(touchNum) - textRect.width() / 2 - xYTextMargin * 2;
        if (touchX < tableMargin + textRect.width() / 2 + xYTextMargin * 2) {
            slipPriceLeft = tableMargin;
        } else if (touchX > viewWidth - tableMargin - xYTextMargin * 2 - textRect.width() / 2) {
            slipPriceLeft = viewWidth - tableMargin - xYTextMargin * 4 - textRect.width();
        }

        slipPriceTop = titleTableHeight + topTableHeight;
        slipPriceBottom = titleTableHeight + topTableHeight + timeTableHeight;
        slipPriceRight = slipPriceLeft + textRect.width() + xYTextMargin * 4;

        canvas.drawRect(slipPriceLeft, slipPriceTop, slipPriceRight, slipPriceBottom, slipAreaPaint);
        path.moveTo(slipPriceLeft, slipPriceTop);
        path.lineTo(slipPriceRight, slipPriceTop);
        path.lineTo(slipPriceRight, slipPriceBottom);
        path.lineTo(slipPriceLeft, slipPriceBottom);
        path.lineTo(slipPriceLeft, slipPriceTop);
        canvas.drawPath(path, xYTextPaint);
        path.reset();
        canvas.drawText(timeList.get(touchNum),
                (slipPriceLeft + xYTextMargin * 2), (slipPriceBottom + 1 - textRect.height() / 2), xYTextPaint);
    }

    /**
     * 绘制滑动价格值
     */
    private void drawSlipPrice(Canvas canvas) {
        if (touchY <= titleTableHeight * 3 / 2) {
            slipPriceTop = titleTableHeight;
            slipPriceBottom = timeTableHeight + titleTableHeight;
            slipPrice = titleTableHeight + timeTableHeight / 2;
        } else if (touchY >= titleTableHeight + topTableHeight - timeTableHeight / 2) {
            slipPriceTop = titleTableHeight + topTableHeight - timeTableHeight;
            slipPriceBottom = titleTableHeight + topTableHeight;
            slipPrice = titleTableHeight + topTableHeight - timeTableHeight / 2;
        } else {
            slipPriceTop = (touchY - titleTableHeight / 2);
            slipPriceBottom = (touchY + timeTableHeight / 2);
            slipPrice = touchY + 1;
        }

        String slipPriceValue = getSlipPriceValue();
        xYTextPaint.getTextBounds(slipPriceValue, 0, slipPriceValue.length(), textRect);

        if (touchX < viewWidth / 3) {
            slipPriceLeft = viewWidth - textRect.width() - (tableMargin + 1) * 11;
            slipPriceRight = viewWidth - tableMargin - 1;
        } else {
            slipPriceLeft = tableMargin + 1;
            slipPriceRight = slipPriceLeft + textRect.width() + (tableMargin + 1) * 11;
        }

        //横线
        canvas.drawLine(tableMargin, slipPrice, (viewWidth - tableMargin), slipPrice, slipPaint);

        canvas.drawRect(slipPriceLeft, slipPriceTop, slipPriceRight, slipPriceBottom, slipAreaPaint);
        path.moveTo(slipPriceLeft, slipPriceTop);
        path.lineTo(slipPriceRight, slipPriceTop);
        path.lineTo(slipPriceRight, slipPriceBottom);
        path.lineTo(slipPriceLeft, slipPriceBottom);
        path.lineTo(slipPriceLeft, slipPriceTop);
        canvas.drawPath(path, xYTextPaint);
        path.reset();
        canvas.drawText(slipPriceValue, (slipPriceLeft + (tableMargin + 1) * 4), (slipPrice + textRect.height() / 2), xYTextPaint);
    }

    /**
     * 滑动价格
     */
    private String getSlipPriceValue() {
        if (touchY <= titleTableHeight) {
            return UiUtil.decimalFormat(maxStockPrice);
        } else if (touchY >= titleTableHeight + topTableHeight) {
            return UiUtil.decimalFormat(minStockPrice);
        }
        return UiUtil.decimalFormat(
                (maxStockPrice - (touchY - titleTableHeight) / topTableHeight * (maxStockPrice - minStockPrice)));
    }

    /**
     * 绘制滑动成交量值
     */
    private void drawSlipVolume(Canvas canvas) {
        if (touchY <= titleTableHeight + topTableHeight + timeTableHeight * 3 / 2) {
            slipPriceTop = titleTableHeight + topTableHeight + timeTableHeight;
            slipPriceBottom = titleTableHeight + topTableHeight + timeTableHeight + timeTableHeight;
            slipPrice = titleTableHeight + topTableHeight + timeTableHeight + timeTableHeight / 2;
        } else if (touchY >= viewHeight - timeTableHeight / 2) {
            slipPriceTop = viewHeight - timeTableHeight;
            slipPriceBottom = viewHeight;
            slipPrice = viewHeight - timeTableHeight / 2;
        } else {
            slipPriceTop = (touchY - timeTableHeight / 2);
            slipPriceBottom = (touchY + timeTableHeight / 2);
            slipPrice = touchY + 1;
        }

        String slipPriceVolume = getSlipPriceVolume();
        xYTextPaint.getTextBounds(slipPriceVolume, 0, slipPriceVolume.length(), textRect);

        if (touchX < viewWidth / 3) {
            slipPriceLeft = viewWidth - textRect.width() - (tableMargin + 1) * 11;
            slipPriceRight = viewWidth - tableMargin - 1;
        } else {
            slipPriceLeft = tableMargin + 1;
            slipPriceRight = slipPriceLeft + textRect.width() + (tableMargin + 1) * 11;
        }

        //横线
        canvas.drawLine(tableMargin, slipPrice, (viewWidth - tableMargin), slipPrice, slipPaint);
        canvas.drawRect(slipPriceLeft, slipPriceTop, slipPriceRight, slipPriceBottom, slipAreaPaint);
        path.moveTo(slipPriceLeft, slipPriceTop);
        path.lineTo(slipPriceRight, slipPriceTop);
        path.lineTo(slipPriceRight, slipPriceBottom);
        path.lineTo(slipPriceLeft, slipPriceBottom);
        path.lineTo(slipPriceLeft, slipPriceTop);
        canvas.drawPath(path, xYTextPaint);
        path.reset();

        canvas.drawText(slipPriceVolume, (slipPriceLeft + (tableMargin + 1) * 4),
                (slipPrice + textRect.height() / 2), xYTextPaint);
    }

    /**
     * 滑动成交量
     */
    private String getSlipPriceVolume() {
        int max = (int) (isOtherCode ? maxStockVolume : maxStockVolume / 100);
        if (touchY <= titleTableHeight + topTableHeight + timeTableHeight) {
            return UiUtil.VOLUME_DECIMAL_FORMAT.format(max);
//            return UiUtil.getVolume(max);
        } else if (touchY >= viewHeight) {
            return UiUtil.VOLUME_DECIMAL_FORMAT.format(0);
//            return UiUtil.getVolume(0);
        }
        return UiUtil.VOLUME_DECIMAL_FORMAT.format((max - (touchY - titleTableHeight - topTableHeight - timeTableHeight)
                / bottomTableHeight * max));
//        return UiUtil.getVolume((int) (max - (touchY - titleTableHeight - topTableHeight - timeTableHeight)
//                / bottomTableHeight * max));
    }

    /**
     * 滑动事件
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                touchX = event.getX();
                touchY = event.getY();
                if (touchX < 2 || touchX > getWidth() - 2) {
                    return false;
                }
                isShowDetails = true;
                postInvalidate();
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                isShowDetails = false;
                postInvalidate();
                break;

            default:
                break;
        }

        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(isShowDetails);
        return super.dispatchTouchEvent(ev);
    }

    public void setDate(Time time) {
        if (time != null) {
            isOtherCode = time.getCode().startsWith(BK) || time.getCode().startsWith(SH) || time.getCode().startsWith(SZ);
            stockPriceList.clear();
            stockAvePriceList.clear();
            stockVolumeList.clear();
            timeList.clear();
            List<Time.DataBean> dataBeanList = time.getData();
            for (int i = 0; i < dataBeanList.size(); i++) {
                addStockPrice(dataBeanList.get(i).getTrade(), i);
                stockAvePriceList.add(dataBeanList.get(i).getAvgPrice());
                stockVolumeList.add(dataBeanList.get(i).getVolume());
                timeList.add(dataBeanList.get(i).getDateTime().substring(8, 10)
                        + ":" + dataBeanList.get(i).getDateTime().substring(10));
            }
            lastClose = time.getSettlement();

            initData();
        }
        invalidate();
        startBeat();
    }

    private void addStockPrice(float trade, int position) {
        stockPriceList.add(trade);

        if (position == 0) {
            maxStockPrice = trade;
            minStockPrice = trade;
        }

        if (maxStockPrice < trade) {
            maxStockPrice = trade;
        } else if (minStockPrice > trade) {
            minStockPrice = trade;
        }
    }

    private void initData() {
        if (Math.abs(minStockPrice - lastClose) > Math.abs(maxStockPrice - lastClose)) {
            float temp = maxStockPrice;
            maxStockPrice = minStockPrice;
            minStockPrice = temp;
        }

        if (maxStockPrice > lastClose) {
            minStockPrice = lastClose * 2 - maxStockPrice;
        } else {
            minStockPrice = maxStockPrice;
            maxStockPrice = lastClose * 2 - maxStockPrice;
        }

        // 百分比坐标值
        percent = decimalFormat.format((maxStockPrice - lastClose) / lastClose * 100) + "%";

        // 找到最大成交量
        for (Float stockVolume : stockVolumeList) {
            maxStockVolume = Math.max(maxStockVolume, stockVolume);
        }
        if (isOtherCode) {
            maxStockVolumeString = UiUtil.getVolume((int) maxStockVolume);
            centreStockVolumeString = UiUtil.getVolume((int) maxStockVolume / 2);
        } else {
            maxStockVolumeString = UiUtil.getVolume((int) maxStockVolume / 100);
            centreStockVolumeString = UiUtil.getVolume((int) maxStockVolume / 200);
        }
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void startBeat() {
        stopBeat();
        if (!timeList.isEmpty() && isBeatTime()) {
            isBeat = true;
            beatHandler.post(beatRunnable);
        }
    }

    private boolean isBeatTime() {
        return !"11:30".equals(timeList.get(timeList.size() - 1)) && !"15:00".equals(timeList.get(timeList.size() - 1));
    }

    public void stopBeat() {
        isBeat = false;
        beatHandler.removeCallbacks(beatRunnable);
    }
}
