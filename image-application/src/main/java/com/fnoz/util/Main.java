package com.fnoz.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException {

        String originalString = "Я ЗНАЮ ЧТО КОЛЯ ПИДОР\nАЛЛО СУЧКА\nПОШЕЛ НАХЕР";
        String[] substrings = originalString.split("\n");

        int fontSize = 9;
        Font font = getFontFromFile("font/rus-small.ttf", fontSize);
        int pictureSize = 60;
        int textWidth = Arrays.stream(substrings).mapToInt(String::length).max().getAsInt() * 6;
        int textHeight = (substrings.length + 1) * fontSize;



        BufferedImage textImage = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_3BYTE_BGR);

        Graphics2D g = textImage.createGraphics();
        g.setPaint(Color.white);
        g.fillRect(0, 0, textImage.getWidth(), textImage.getHeight());

        g.setColor(Color.black);
        g.setFont(font);
        // g.setFont(new Font("ARI", Font.PLAIN, 9));
        int textY = 0;
        for (String str: originalString.split("\n")) {
            g.drawString(str, 1, textY += g.getFontMetrics().getHeight());
            // g.drawString(str, 1,8);
        }

        int[] array = null;
        int maxX = 0;
        int maxY = 0;
        for (int y = 0; y < textHeight; y++) {
            for (int x = 0; x < textWidth; x++) {
                int[] pixel = textImage.getData().getPixel(x, y, array);
                if (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0) {
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                }
            }
        }
        maxX+=3;
        maxY+=3;
        // System.out.println(Arrays.toString(resizedImage.getData().getPixel(1, 9, array)));

        BufferedImage pictureImage = new BufferedImage(maxX * pictureSize, maxY * pictureSize, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D pictureImageGraphics = pictureImage.createGraphics();
        pictureImageGraphics.setPaint(Color.white);
        pictureImageGraphics.fillRect(0, 0, pictureImage.getWidth(), pictureImage.getHeight());


        BufferedImage imageFrame = ImageIO.read(new File("original.png"));
        BufferedImage resizedImage = new BufferedImage(pictureSize, pictureSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImage.createGraphics();
        g2.drawImage(imageFrame, 0, 0, pictureSize, pictureSize, null);
        g2.dispose();

        for (int y = 0; y < textHeight; y++) {
            for (int x = 0; x < textWidth; x++) {
                int[] pixel = textImage.getData().getPixel(x, y, array);
                if (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0) {
                    pictureImageGraphics.drawImage(resizedImage, x * pictureSize, y * pictureSize, null);
                }
            }
        }

        ImageIO.write(pictureImage,"png", new File("lox.png"));

    }

    public static Font getFontFromFile(String fileName, int size) {
        try {
            Font font = Font.createFont(Font.PLAIN, new File(fileName));
            font = font.deriveFont(Font.PLAIN, size);
            return font;
        } catch (FontFormatException | IOException ffe) {
            ffe.printStackTrace();
        }
        return null;
    }
}