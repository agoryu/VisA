import vtk.*;

public class Test {
	
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
		
		vtkSphereSource sphere = new vtkSphereSource();
		sphere.SetRadius(1.0);
		sphere.SetThetaResolution(18);
		sphere.SetPhiResolution(18);
		
		vtkPolyDataMapper map = new vtkPolyDataMapper();
		map.SetInput(sphere.GetOutput());
		
		vtkActor aSphere = new vtkActor();
		aSphere.SetMapper(map);
		aSphere.GetProperty().SetColor(0, 0, 1);
		
		vtkRenderer ren1 = new vtkRenderer();
		ren1.AddActor(aSphere);
		ren1.SetBackground(1, 1, 1);
		
		vtkRenderWindow renWin = new vtkRenderWindow();
		renWin.AddRenderer(ren1);
		renWin.SetSize(300,300);
		
		vtkRenderWindowInteractor iren = new vtkRenderWindowInteractor();
		iren.SetRenderWindow(renWin);
		renWin.Render();
		iren.Start();

	}

}
