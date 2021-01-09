/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.scanner.opencv;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import bagaturchess.scanner.cnn.impl.utils.ScannerUtils;
import bagaturchess.scanner.patterns.api.ImageHandlerSingleton;


public class OpenCVUtils {
	
	
	public static Mat bufferedImage2Mat(BufferedImage image) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ImageIO.write(image, "png", byteArrayOutputStream);
		byteArrayOutputStream.flush();
		return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
	}
	
	
	public static BufferedImage mat2BufferedImage(Mat matrix) throws IOException {
		MatOfByte mob = new MatOfByte();
		Imgcodecs.imencode(".png", matrix, mob);
		return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
	}
	
	
	public static BufferedImage createPieceImage(String pieceSetName, int pid, int bgcolor, int size) throws IOException {
		Image piece = (Image) ImageHandlerSingleton.getInstance().loadPieceImageFromMemory(pid, pieceSetName, size);
		BufferedImage imagePiece = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
		Graphics g = imagePiece.getGraphics();
		g.setColor((Color)ImageHandlerSingleton.getInstance().getColor(bgcolor));
		g.fillRect(0, 0, imagePiece.getWidth(), imagePiece.getHeight());
		Image pieceScaled = piece;
		g.drawImage(pieceScaled, 0, 0, null);
		return imagePiece;
	}
}
