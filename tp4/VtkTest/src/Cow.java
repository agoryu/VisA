import vtk.vtkActor;
import vtk.vtkDecimatePro;
import vtk.vtkPLYWriter;
import vtk.vtkPolyDataMapper;
import vtk.vtkPolyDataNormals;
import vtk.vtkRenderWindow;
import vtk.vtkRenderWindowInteractor;
import vtk.vtkRenderer;
import vtk.vtkSmoothPolyDataFilter;
import vtk.vtkTriangleFilter;
import vtk.vtkXMLPolyDataReader;

public class Cow {

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
		
		//vtkOBJReader cow = new vtkOBJReader();
		vtkXMLPolyDataReader cow = new vtkXMLPolyDataReader();
		cow.SetFileName("/home/m2ivi/vanegue/Documents/VisA/tp4/3Dmodels/cow.vtp");
		//cow.Update();
		//cow.GetOutput().GetPointData().SetNormals(null);
		
		vtkTriangleFilter cowTriangle = new vtkTriangleFilter();
		cowTriangle.SetInput(cow.GetOutput());
		cowTriangle.Update();
		
		System.out.println("nb point -> " + cowTriangle.GetOutput().GetNumberOfPoints());
		System.out.println("nb face -> " + cowTriangle.GetOutput().GetNumberOfPolys());
		
		vtkDecimatePro dp = new vtkDecimatePro();
		dp.SetInput(cowTriangle.GetOutput());
		dp.SetTargetReduction(0.50);
		dp.Update();
		
		System.out.println("nb point -> " + dp.GetOutput().GetNumberOfPoints());
		System.out.println("nb face -> " + dp.GetOutput().GetNumberOfPolys());
		
		vtkSmoothPolyDataFilter spdf = new vtkSmoothPolyDataFilter();
		spdf.SetInput(dp.GetOutput());
		spdf.SetNumberOfIterations(10);
		
		vtkPolyDataNormals normal = new vtkPolyDataNormals();
		normal.SetInput(spdf.GetOutput());
		normal.ComputePointNormalsOn();
		normal.ComputeCellNormalsOff();
		normal.Update();
		
		vtkPLYWriter writer = new vtkPLYWriter();
		writer.SetInput(normal.GetOutput());
		writer.SetFileName("cow");
		writer.Write();
		
		vtkPolyDataMapper map = new vtkPolyDataMapper();
		map.SetInput(normal.GetOutput());
		
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
