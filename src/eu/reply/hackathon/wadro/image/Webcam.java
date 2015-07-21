package eu.reply.hackathon.wadro.image;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;


public class Webcam {

	public static void main (String args[]) throws InterruptedException{

		//System.out.println("Hello, OpenCV");
		// Load the native library.
		//System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture camera = new VideoCapture(0);
		Thread.sleep(1000);
		camera.open(0); //Useless
		//int minHessian = 500;

		
		Mat objImg = Imgcodecs.imread("C:\\Users\\d.dandrea\\Downloads\\20150712_205514.jpg", Imgcodecs.IMREAD_GRAYSCALE);
		Mat currFrame = new Mat();

		//Mat objectMat = null;
		//surf.detect(objectMat, keypointsObject);
		
		if(!camera.isOpened()){
			//System.out.println("Camera Error");
		}
		else{
			//System.out.println("Camera OK?");
		}


		//camera.grab();
		////System.out.println("Frame Grabbed");
		//camera.retrieve(frame);
		////System.out.println("Frame Decoded");
		JFrame jframe=new JFrame();
		JLabel lbl=new JLabel();
		jframe.setLayout(new FlowLayout());        
		jframe.setSize(currFrame.width()+50, currFrame.height()+50); 

		while(true){
			camera.read(currFrame);
			Imgproc.cvtColor(currFrame, currFrame, Imgproc.COLOR_RGB2GRAY);

			//System.out.println("Frame Obtained");
			//No difference camera.release();
			
			//System.out.println("Captured Frame Width " + currFrame.width());

			Mat currMatchedFrame = findMatchingPoint(objImg, currFrame);
			displayImage(jframe,lbl,Mat2BufferedImage(currMatchedFrame));
			// //System.out.println("OK");
		}
	}

	public static BufferedImage Mat2BufferedImage(Mat m){
		// source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
		// Fastest code
		// The output can be assigned either to a BufferedImage or to an Image
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if ( m.channels() > 1 ) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels()*m.cols()*m.rows();
		byte [] b = new byte[bufferSize];
		m.get(0,0,b); // get all the pixels
		if(m.width()>0){
		BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);  
		return image;
		}
		else return null;

	}
	public static void displayImage(JFrame frame,JLabel lbl, Image img2)
	{   
		//BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
		if(img2!=null){
		ImageIcon icon=new ImageIcon(img2);
		lbl.setIcon(icon);
		frame.add(lbl);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}
	public static Mat findMatchingPoint(Mat objImg,Mat sceneImg){

	    FeatureDetector detector = FeatureDetector.create(FeatureDetector.BRISK);
	    DescriptorExtractor descriptor = DescriptorExtractor.create(DescriptorExtractor.BRISK);
	    DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);

	    Mat outputImg = new Mat();

	    
	    //set up img1 (scene)
	    Mat descriptors1 = new Mat();
	    MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
	    //calculate descriptor for img1
	    detector.detect(objImg, keypoints1);
	    descriptor.compute(objImg, keypoints1, descriptors1);

	    //set up img2 (template)
	    Mat descriptors2 = new Mat();
	    MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
	    //calculate descriptor for img2
	    detector.detect(sceneImg, keypoints2);
	    descriptor.compute(sceneImg, keypoints2, descriptors2);

	    //match 2 images' descriptors
	    MatOfDMatch matches = new MatOfDMatch();
	    if(descriptors1.type() == descriptors2.type() &&
	    		descriptors1.cols() == descriptors2.cols())
	    {
	    	matcher.match(descriptors1, descriptors2,matches);

	    //calculate max and min distances between keypoints
	    double max_dist=0;
	    double min_dist=99;

	    List<DMatch> matchesList = matches.toList();
	    for(int i=0;i<descriptors1.rows();i++)
	    {
	        if (matchesList.size()>0){
	    	double dist = matchesList.get(i).distance;
	        if (dist<min_dist) min_dist = dist;
	        if (dist>max_dist) max_dist = dist;
	        }
	    }

	    //set up good matches, add matches if close enough
	    LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
	    MatOfDMatch gm = new MatOfDMatch();
	    for (int i=0;i<descriptors2.rows();i++)
	    {
	        if(matchesList.size()>i && matchesList.get(i).distance<3*min_dist)
	        {
	    	    //System.out.println("1");

	            good_matches.addLast(matchesList.get(i));
	        }
	    }
	    Collections.sort(good_matches,new Comparator<DMatch>() {
	        @Override
	        public int compare(DMatch o1, DMatch o2) {
	            if(o1.distance<o2.distance)
	                return -1;
	            if(o1.distance>o2.distance)
	                return 1;
	            return 0;
	        }
	    });
	    if(good_matches.size()>30){
	    	good_matches =  new LinkedList(good_matches.subList(0, 30));
	    }
	    gm.fromList(good_matches);
	    
	    //put keypoints mats into lists
	    List<KeyPoint> keypoints1_List = keypoints1.toList();
	    List<KeyPoint> keypoints2_List = keypoints2.toList();

	    //put keypoints into point2f mats so calib3d can use them to find homography
	    LinkedList<Point> objList = new LinkedList<Point>();
	    LinkedList<Point> sceneList = new LinkedList<Point>();
	    
	    //System.out.println(good_matches.size());
	    for(int i=0;i<good_matches.size();i++)
	    {
	        objList.addLast(keypoints2_List.get(good_matches.get(i).queryIdx).pt);
	        sceneList.addLast(keypoints1_List.get(good_matches.get(i).trainIdx).pt);
	    }
	    MatOfPoint2f obj = new MatOfPoint2f();
	    MatOfPoint2f scene = new MatOfPoint2f();
	    obj.fromList(objList);
	    scene.fromList(sceneList);
	    
	    //System.out.println(objList.toString());
	    //output image
	    MatOfByte drawnMatches = new MatOfByte();
	   // if(good_matches.size()>100)
	    Features2d.drawMatches(objImg, keypoints1, sceneImg, keypoints2, gm, outputImg, Scalar.all(-1), Scalar.all(-1), drawnMatches,Features2d.NOT_DRAW_SINGLE_POINTS);
	    //else 
	    //	Features2d.drawMatches(objImg, new MatOfKeyPoint(), sceneImg,  new MatOfKeyPoint(), gm, outputImg, Scalar.all(-1), Scalar.all(-1), drawnMatches,Features2d.NOT_DRAW_SINGLE_POINTS);

	    //run homography on object and scene points
	    //Mat H = Calib3d.findHomography(obj, scene,Calib3d.RANSAC, 5);
	   // Mat tmp_corners = new Mat(4,1,CvType.CV_32FC2);
	    //Mat scene_corners = new Mat(4,1,CvType.CV_32FC2);

	    //get corners from object
	    //tmp_corners.put(0, 0, new double[] {0,0});
	    //tmp_corners.put(1, 0, new double[] {sceneImg.cols(),0});
	    //tmp_corners.put(2, 0, new double[] {sceneImg.cols(),sceneImg.rows()});
	   // tmp_corners.put(3, 0, new double[] {0,sceneImg.rows()});

	   // Core.perspectiveTransform(tmp_corners,scene_corners, H);

	    }
	    return outputImg;
	   // Core.line(outputImg, new Point(scene_corners.get(0,0)), new Point(scene_corners.get(1,0)), new Scalar(0, 255, 0),4);
	  //  Core.line(outputImg, new Point(scene_corners.get(1,0)), new Point(scene_corners.get(2,0)), new Scalar(0, 255, 0),4);
	   // Core.line(outputImg, new Point(scene_corners.get(2,0)), new Point(scene_corners.get(3,0)), new Scalar(0, 255, 0),4);
	   // Core.line(outputImg, new Point(scene_corners.get(3,0)), new Point(scene_corners.get(0,0)), new Scalar(0, 255, 0),4);
	}
}