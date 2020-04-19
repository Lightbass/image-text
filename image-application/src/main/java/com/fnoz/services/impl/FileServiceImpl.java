package com.fnoz.services.impl;

import com.fnoz.services.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static com.fnoz.util.UtilService.checkFolders;
import static com.fnoz.util.UtilService.copyFileToPathAndGetName;

@Service
public class FileServiceImpl implements FileService {

    @Value("${spring.images_dir}")
    private String IMAGES_DIR;

    @Value(value = "classpath:font/rus-small.ttf")
    private Resource rusFont;

    private Path rootLocation;

    public FileServiceImpl() {
    }

    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(IMAGES_DIR);
        checkFolders(rootLocation);
    }

    @Override
    public void saveImage(MultipartFile file) {
        //DocumentDTO documentDTO = new DocumentDTO();
        System.out.println(("/api/files/images/" + copyFileToPathAndGetName(file, this.rootLocation)));


    }

    @Override
    public byte[] saveImageText(MultipartFile file, String text, Integer x, Integer y, Integer size) {
        try {
            File imageFile = new File("original.png");
            return drawImage(text, file.getInputStream(), size, x, y);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }

    private byte[] drawImage(String originalString, InputStream imageFile, Integer size, Integer x, Integer y)
            throws IOException {

        String[] substrings = originalString.split("\n");

        int fontSize = 9;
        Font font = getFontFromFile(this.rusFont.getInputStream(), fontSize);
        int pictureSize = 60;
        int textWidth = Arrays.stream(substrings).mapToInt(String::length).max().getAsInt() * 6;
        int textHeight = (substrings.length + 1) * fontSize;


        BufferedImage textImage = new BufferedImage(textWidth, textHeight, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D g = textImage.createGraphics();
        g.setPaint(Color.white);
        g.fillRect(0, 0, textImage.getWidth(), textImage.getHeight());
        g.setColor(Color.black);
        g.setFont(font);
        int textY = 0;
        for (String str: originalString.split("\n")) {
            g.drawString(str, 1, textY += g.getFontMetrics().getHeight());
        }

        int[] sizeArray = this.getMaxHeightAndWidth(textWidth, textHeight, textImage);

        // System.out.println(Arrays.toString(resizedImage.getData().getPixel(1, 9, array)));

        BufferedImage pictureImage = new BufferedImage(sizeArray[0] * pictureSize,
                sizeArray[1] * pictureSize, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D pictureImageGraphics = pictureImage.createGraphics();
        pictureImageGraphics.setPaint(Color.white);
        pictureImageGraphics.fillRect(0, 0, pictureImage.getWidth(), pictureImage.getHeight());

        BufferedImage imageFrame = ImageIO.read(imageFile);
        imageFrame = imageFrame.getSubimage(x, y, size, size);

        BufferedImage scaledImageFrame = new BufferedImage(pictureSize, pictureSize, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        double scaleFactor = ((double) pictureSize) / ((double) size);
        at.scale(scaleFactor, scaleFactor);
        AffineTransformOp ato = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
        scaledImageFrame = ato.filter(imageFrame, scaledImageFrame);

        BufferedImage resizedImage = new BufferedImage(pictureSize, pictureSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImage.createGraphics();
        g2.drawImage(imageFrame, 0, 0, 60, 60, null);
        g2.dispose();

        this.drawImageByBlackPixel(textWidth, textHeight, pictureImageGraphics, textImage, scaledImageFrame);

        ImageIO.write(pictureImage,"png", new File("lox.png"));
        return this.getBytesFromImage(pictureImage);
    }

    private Font getFontFromFile(InputStream inputStream, int size) {
        try {
            Font font = Font.createFont(Font.PLAIN, inputStream);
            font = font.deriveFont(Font.PLAIN, size);
            return font;
        } catch (FontFormatException | IOException ffe) {
            ffe.printStackTrace();
        }
        return null;
    }

    private int[] getMaxHeightAndWidth(int width, int height, BufferedImage image) {
        int[] array = null;
        int maxX = 0;
        int maxY = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] pixel = image.getData().getPixel(x, y, array);
                if (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0) {
                    maxX = Math.max(x, maxX);
                    maxY = Math.max(y, maxY);
                }
            }
        }
        maxX+=2;
        maxY+=4;
        return new int[] { maxX, maxY };
    }

    private void drawImageByBlackPixel(int width, int height, Graphics2D resultImage, BufferedImage dataImage,
                                       BufferedImage frame) {
        int[] array = null;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int[] pixel = dataImage.getData().getPixel(x, y, array);
                if (pixel[0] == 0 && pixel[1] == 0 && pixel[2] == 0) {
                    resultImage.drawImage(frame, x * 60, y * 60, null);
                }
            }
        }
    }

    private byte[] getBytesFromImage(BufferedImage image) {
        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, "jpg", baos);
            return baos.toByteArray();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
}
