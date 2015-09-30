import vtk.*;

public class Torus {

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
		
		//creation du torus
		vtkSuperquadricSource torus = new vtkSuperquadricSource();
		double[] center = {0,0,0};
		torus.SetCenter(center);
		torus.SetThetaResolution(64);
		torus.SetPhiResolution(64);
		torus.ToroidalOn();
		
		//suppression discontinuite
		vtkCleanPolyData torusClean = new vtkCleanPolyData();
		torusClean.SetTolerance(0.005);
		torusClean.SetInput(torus.GetOutput());
		
		//utilisation de la courbure gaussian
		vtkCurvatures torusGaussian = new vtkCurvatures();
		torusGaussian.SetCurvatureTypeToGaussian();
		torusGaussian.SetInput(torusClean.GetOutput());
		
		//creation de la lut
		vtkLookupTable lut = new vtkLookupTable();
		lut.SetNumberOfColors(256);
		lut.SetHueRange(0.15, 1.0);
		lut.SetSaturationRange(1.0, 1.0);
		lut.SetValueRange(1.0, 1.0);
		lut.SetAlphaRange(1.0, 1.0);
		lut.SetRange(-20, 20);
		
		//creation des primitives opengl
		vtkPolyDataMapper map = new vtkPolyDataMapper();
		map.SetLookupTable(lut);
		map.SetUseLookupTableScalarRange(1);
		map.SetInput(torusGaussian.GetOutput());
		
		//creation de l'acteur qui permet de position, colorer et ajouter des propriété graphique
		vtkActor aTorus = new vtkActor();
		aTorus.SetMapper(map);
		aTorus.GetProperty().SetColor(0,0,1);
		
		
		vtkRenderer ren1 = new vtkRenderer();
		ren1.AddActor(aTorus);
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
