import vtk.vtkActor;
import vtk.vtkIterativeClosestPointTransform;
import vtk.vtkPLYReader;
import vtk.vtkPolyDataMapper;
import vtk.vtkRenderWindow;
import vtk.vtkRenderWindowInteractor;
import vtk.vtkRenderer;
import vtk.vtkTransformPolyDataFilter;

public class Face {
	
	static {
		System.loadLibrary("awt");
		System.loadLibrary("vtkCommonJava");
		System.loadLibrary("vtkFilteringJava");
		System.loadLibrary("vtkIOJava");
		System.loadLibrary("vtkImagingJava");
		System.loadLibrary("vtkGraphicsJava");
		System.loadLibrary("vtkRenderingJava");
		System.loadLibrary("vtkHybridJava");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		vtkPLYReader face1 = new vtkPLYReader();
		face1.SetFileName("/home/m2ivi/vanegue/Documents/VisA/tp4/3Dmodels/Model1.ply");
		face1.Update();
		vtkPLYReader face2 = new vtkPLYReader();
		face2.SetFileName("/home/m2ivi/vanegue/Documents/VisA/tp4/3Dmodels/Model2.ply");
		face2.Update();

		vtkIterativeClosestPointTransform recal = new vtkIterativeClosestPointTransform();
		recal.SetSource(face1.GetOutput());
		recal.SetTarget(face2.GetOutput());
		recal.SetMaximumNumberOfLandmarks(5000);
		recal.GetLandmarkTransform().SetModeToRigidBody();
		recal.StartByMatchingCentroidsOn();
		recal.SetMaximumNumberOfIterations(1);
		recal.Modified();
		recal.Update();
		
		vtkTransformPolyDataFilter tpd = new vtkTransformPolyDataFilter();
		tpd.SetInput(face1.GetOutput());
		tpd.SetTransform(recal);
		tpd.Update();
		
		vtkPolyDataMapper map = new vtkPolyDataMapper();
		map.SetInput(tpd.GetOutput());
		vtkPolyDataMapper map2 = new vtkPolyDataMapper();
		map2.SetInput(face2.GetOutput());
		
		vtkActor actorFace1 = new vtkActor();
		actorFace1.SetMapper(map);
		actorFace1.GetProperty().SetColor(0, 0, 1);
		vtkActor actorFace2 = new vtkActor();
		actorFace2.SetMapper(map2);
		actorFace2.GetProperty().SetColor(0, 1, 0);
		
		
		vtkRenderer ren1 = new vtkRenderer();
		ren1.AddActor(actorFace1);
		ren1.AddActor(actorFace2);
		ren1.SetBackground(1, 1, 1);
		
		vtkRenderWindow renWin = new vtkRenderWindow();
		renWin.AddRenderer(ren1);
		renWin.SetSize(500,500);
		
		vtkRenderWindowInteractor iren = new vtkRenderWindowInteractor();
		iren.SetRenderWindow(renWin);
		renWin.Render();
		iren.Start();

	}

}
