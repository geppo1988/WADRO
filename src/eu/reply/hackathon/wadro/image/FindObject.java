package eu.reply.hackathon.wadro.image;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

class FindObject {
	public static void main (String args[]) throws InterruptedException{

		System.out.println("Hello, OpenCV");
		// Load the native library.
		System.out.println(System.getProperty("java.library.path"));
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		VideoCapture camera = new VideoCapture(0);
		Thread.sleep(1000);
		camera.open(0); 
		Mat currFrame = new Mat();

		if(!camera.isOpened()){
			System.out.println("Camera Error");
		}
		else{
			System.out.println("Camera OK?");
		}


		//camera.grab();
		////System.out.println("Frame Grabbed");
		//camera.retrieve(frame);
		////System.out.println("Frame Decoded");
		JFrame jframe=new JFrame();
		JLabel lbl=new JLabel();
		jframe.setLayout(new FlowLayout());   
		
		JFrame jframe2=new JFrame();
		JLabel lbl2=new JLabel();
		jframe2.setLayout(new FlowLayout()); 

		Mat img_object = Imgcodecs.imread("C:\\Users\\d.dandrea\\Desktop\\WadroWorkspace\\img\\logo.jpg", Imgcodecs.IMREAD_GRAYSCALE);

		while(true){
			camera.read(currFrame);
			Imgproc.cvtColor(currFrame, currFrame, Imgproc.COLOR_RGB2GRAY);

			System.out.println("Frame Obtained");
			//No difference camera.release();
			List<Mat> coupleImage = findSign(currFrame,img_object);
			Mat outputMatchPoints = coupleImage.get(0);
			Mat outputObjectIdentify = coupleImage.get(1);
			refreshJFrame(jframe,lbl,Mat2BufferedImage(outputMatchPoints),outputMatchPoints.width(),outputMatchPoints.height());	
			refreshJFrame(jframe2,lbl2,Mat2BufferedImage(outputObjectIdentify),outputObjectIdentify.width(),outputObjectIdentify.height());

			// //System.out.println("OK");
		}

	}
	//
	public static List<Mat> findSign(Mat img_scene,Mat img_object) {
		List<Mat> coupleImage = new LinkedList<Mat>();
		System.out.println("\nRunning FindObject");

		//Sostituire con un altra immagine

		//	img_scene = Imgcodecs.imread("C:\\Users\\d.dandrea\\Downloads\\20150712_205511.jpg", Imgcodecs.IMREAD_GRAYSCALE);


		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB); //4 = SURF 

		MatOfKeyPoint keypoints_object = new MatOfKeyPoint();
		MatOfKeyPoint keypoints_scene  = new MatOfKeyPoint();

		detector.detect(img_object, keypoints_object);
		detector.detect(img_scene, keypoints_scene);

		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB); //2 = SURF;

		Mat descriptor_object = new Mat();
		Mat descriptor_scene = new Mat() ;

		extractor.compute(img_object, keypoints_object, descriptor_object);
		extractor.compute(img_scene, keypoints_scene, descriptor_scene);

		
		
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE); // 1 = FLANNBASED
		MatOfDMatch matches = new MatOfDMatch();

		Mat img_matches = new Mat();
		
		coupleImage.add(img_scene);
		coupleImage.add(img_matches);

		if(descriptor_object.type() == descriptor_scene.type() &&
				descriptor_object.cols() == descriptor_scene.cols())
		{
			matcher.match(descriptor_object, descriptor_scene, matches);
			List<DMatch> matchesList = matches.toList();
			//System.out.println(matchesList.toString());
			Double max_dist = 0.0;
			Double min_dist = 100.0;

			for(int i = 0; i < descriptor_object.rows(); i++){
				if(matchesList.size()>0){
					Double dist = (double) matchesList.get(i).distance;
					if(dist < min_dist) min_dist = dist;
					if(dist > max_dist) max_dist = dist;
				}
			}

			System.out.println("-- Max dist : " + max_dist);
			System.out.println("-- Min dist : " + min_dist);    

			LinkedList<DMatch> good_matches = new LinkedList<DMatch>();
			MatOfDMatch gm = new MatOfDMatch();

			for(int i = 0; i < descriptor_object.rows(); i++){
				if(matchesList.size()>0 && matchesList.get(i).distance < 2.5*min_dist){
					good_matches.addLast(matchesList.get(i));
				}
			}
			if(good_matches.size()>5)
				gm.fromList(good_matches);

			Features2d.drawMatches(
					img_object,
					keypoints_object, 
					img_scene,
					keypoints_scene, 
					gm, 
					img_matches, 
					new Scalar(255,0,0), 
					new Scalar(0,0,255), 
					new MatOfByte(), 
					2);

			LinkedList<Point> objList = new LinkedList<Point>();
			LinkedList<Point> sceneList = new LinkedList<Point>();

			List<KeyPoint> keypoints_objectList = keypoints_object.toList();
			List<KeyPoint> keypoints_sceneList = keypoints_scene.toList();

			for(int i = 0; i<good_matches.size(); i++){
				objList.addLast(keypoints_objectList.get(good_matches.get(i).queryIdx).pt);
				sceneList.addLast(keypoints_sceneList.get(good_matches.get(i).trainIdx).pt);
			}

			MatOfPoint2f obj = new MatOfPoint2f();
			obj.fromList(objList);

			MatOfPoint2f scene = new MatOfPoint2f();
			scene.fromList(sceneList);

			Mat h=new Mat();
			if (good_matches.size()>0){
				h = Calib3d.findHomography(obj, scene, Calib3d.LMEDS, min_dist);
				System.out.println("HOMOGRAFY");
				if(h.width()==3){
					System.out.println("HHH "+h.width());

					Mat obj_corners = new Mat(4,1,CvType.CV_32FC2);
					Mat scene_corners = new Mat(4,1,CvType.CV_32FC2);
					//obj_corners.fromList(cornerList);

					//Mat hg = Calib3d.findHomography(obj,scene);

					obj_corners.put(0, 0, new double[] {0,0});
					obj_corners.put(1, 0, new double[] {img_object.cols(),0});
					obj_corners.put(2, 0, new double[] {img_object.cols(),img_object.rows()});
					obj_corners.put(3, 0, new double[] {0,img_object.rows()});
					


					Core.perspectiveTransform(obj_corners,scene_corners, h);

					Imgproc.line(img_scene, new Point(scene_corners.get(0,0)), new Point(scene_corners.get(1,0)), new Scalar(0, 255, 0),4);
					Imgproc.line(img_scene, new Point(scene_corners.get(1,0)), new Point(scene_corners.get(2,0)), new Scalar(0, 255, 0),4);
					Imgproc.line(img_scene, new Point(scene_corners.get(2,0)), new Point(scene_corners.get(3,0)), new Scalar(0, 255, 0),4);
					Imgproc.line(img_scene, new Point(scene_corners.get(3,0)), new Point(scene_corners.get(0,0)), new Scalar(0, 255, 0),4);
					
				}
			}

		}
		return coupleImage;



		//ERROR HERE :
		//OpenCV Error: Assertion failed (scn + 1 == m.cols && (depth == CV_32F || depth == CV_64F)) in unknown function, file ..\..\..\src\opencv\modules\core\src\matmul.cpp, line 1926

		//Draw the lines... later, when the homography will work
		/*
			Core.line(img_matches, new Point(), new Point(), new ...
		 */
	}
	public static void refreshJFrame(JFrame frame,JLabel lbl, Image img2, int w, int h)
	{   
		//BufferedImage img=ImageIO.read(new File("/HelloOpenCV/lena.png"));
		if(img2!=null){
			ImageIcon icon=new ImageIcon(img2);
			lbl.setIcon(icon);
			frame.setSize(w, h); 
			frame.add(lbl);
			frame.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
}