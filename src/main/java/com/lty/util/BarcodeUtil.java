package com.lty.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * 条形码工具类
 */
public class BarcodeUtil {

    /**
     * 生成CODE128条形码（无底部文字）
     */
    public static void createCode128(String content, int width, int height, String savePath) throws Exception {
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, width, height);
        Path path = FileSystems.getDefault().getPath(savePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * 生成带底部原文的 CODE128 条形码
     * @param content 条码内容
     * @param width 条码宽度
     * @param height 条码高度（不含文字高度）
     * @param savePath 保存路径
     * @throws Exception
     */
    public static void createCode128WithText(String content, int width, int height, String savePath) throws Exception {
        // 1. 生成基础条码
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.CODE_128, width, height);
        BufferedImage barcodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 2. 底部文字高度 + 整体图片高度
        int textHeight = 25;
        int totalHeight = height + textHeight;

        // 3. 创建新图片（条码 + 文字区域）
        BufferedImage combinedImage = new BufferedImage(width, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combinedImage.createGraphics();

        // 背景白色
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, totalHeight);

        // 画上条码
        g2d.drawImage(barcodeImage, 0, 0, null);

        // 4. 绘制底部文字
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        FontMetrics fontMetrics = g2d.getFontMetrics();
        int textX = (width - fontMetrics.stringWidth(content)) / 2; // 水平居中
        int textY = height + ((textHeight - fontMetrics.getHeight()) / 2) + fontMetrics.getAscent();
        g2d.drawString(content, textX, textY);

        g2d.dispose();

        // 5. 保存图片
        Path path = FileSystems.getDefault().getPath(savePath);
        ImageIO.write(combinedImage, "PNG", path.toFile());
    }

    /**
     * 生成纯二维码（无底部文字）
     * @param content 二维码内容(链接/中文/数字)
     * @param size 正方形边长
     * @param savePath 保存路径png
     */
    public static void createQrCode(String content, int size, String savePath) throws Exception {
        QRCodeWriter qrWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        //容错级别：L/M/Q/H，H最高容错
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, size, size, hints);
        Path path = FileSystems.getDefault().getPath(savePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
    }

    /**
     * 生成【底部带文字】二维码，文字居中
     * @param content 二维码存储内容
     * @param qrSize 二维码正方形尺寸
     * @param savePath 保存路径
     */
    public static void createQrCodeWithText(String content, int qrSize, String savePath) throws Exception {
        //1.生成二维码原图
        QRCodeWriter qrWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        BitMatrix bitMatrix = qrWriter.encode(content, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);
        BufferedImage qrImg = MatrixToImageWriter.toBufferedImage(bitMatrix);

        //2.底部文字区域高度
        int textAreaHeight = 30;
        int totalHeight = qrSize + textAreaHeight;
        BufferedImage allImg = new BufferedImage(qrSize, totalHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = allImg.createGraphics();

        //白底
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, qrSize, totalHeight);
        //绘制二维码在上半部分
        g2d.drawImage(qrImg, 0, 0, null);

        //绘制底部居中文字
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        FontMetrics fm = g2d.getFontMetrics();
        int strWidth = fm.stringWidth(content);
        int x = (qrSize - strWidth) / 2;
        int y = qrSize + ((textAreaHeight - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(content, x, y);
        g2d.dispose();

        //保存图片
        ImageIO.write(allImg, "PNG", FileSystems.getDefault().getPath(savePath).toFile());
    }


    // 测试
    public static void main(String[] args) throws Exception {
        // 原来的：无文字
        // createCode128("ABC123456789",300,120,"D:/code128.png");
        // // 新的：带底部原文
        // createCode128WithText("ABC123456789", 300, 100, "D:/code128_with_text.png");

        //1.纯二维码
        createQrCode("https://www.baidu.com",300,"D:/qr_normal.png");
        //2.带底部文字二维码
        createQrCodeWithText("产品编号:PD20260603001",300,"D:/qr_with_text.png");
    }
}