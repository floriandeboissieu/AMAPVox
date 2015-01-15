package fr.ird.jeeb.workspace.archimedes.geometry.utils;

import javax.vecmath.Point3f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

/**
 * Common operations on vector
 * @author Cresson, Sept. 2012
 *
 */
public class VectorUtils {

	/** Scattering Epsilon **/
	public static final float scatEps = 0.01f;
	
	/**
	 * Shifts a point along a specified direction from the constant value "scatEps"
	 * @param p		point
	 * @param dir	direction of shift
	 */
	public static void shift(Point3f p, Vector3f dir) {
		Vector3f epsVect = new Vector3f(dir);
		epsVect.scale(scatEps);
		p.add(epsVect);
	}
	
	/**
	 * @param vec
	 * @return a Vector3f orthogonal to the input vector "vec"
	 */
	public static Vector3f getOrthogonalVector(Tuple3f vec){
		
		// find smallest coordinate (to be set to 0.0)
		if (Math.abs(vec.x) <= Math.abs(vec.y)){
			if (Math.abs(vec.x) <= Math.abs(vec.z))
				return new Vector3f (0, vec.z, -vec.y);
			else
				return new Vector3f (vec.y, -vec.x, 0);
		}
		else{
			if (Math.abs(vec.y) <= Math.abs(vec.z))
				return new Vector3f (vec.z, 0, -vec.x);
			else
				return new Vector3f (vec.y, -vec.x, 0);
		}
	}

	/**
	 * @param vec
	 * @return a Vector3d orthogonal to the input vector "vec"
	 */
	public static Vector3d getOrthogonalVector(Tuple3d vec){
		
		// find smallest coordinate (to be set to 0.0)
		if (Math.abs(vec.x) <= Math.abs(vec.y)){
			if (Math.abs(vec.x) <= Math.abs(vec.z))
				return new Vector3d (0, vec.z, -vec.y);
			else
				return new Vector3d (vec.y, -vec.x, 0);
		}
		else{
			if (Math.abs(vec.y) <= Math.abs(vec.z))
				return new Vector3d (vec.z, 0, -vec.x);
			else
				return new Vector3d (vec.y, -vec.x, 0);
		}
	}


	
	/**
	 * @param vec1: normalized vector
	 * @param vec2: normalized vector
	 * @return cross product of the 2 vectors
	 */
	public static Vector3f getOrthogonalVector (Vector3f vec1, Vector3f vec2) {
		
		Vector3f orthogonalVector = new Vector3f (vec1);
		orthogonalVector.cross (orthogonalVector, vec2);
		
		return orthogonalVector;
	}
	/**
	 * @param vec1: normalized vector
	 * @param vec2: normalized vector
	 * @return cross product of the 2 vectors
	 */
	public static Vector3d getOrthogonalVector (Vector3d vec1, Vector3d vec2) {
		
		Vector3d orthogonalVector = new Vector3d (vec1);
		orthogonalVector.cross (orthogonalVector, vec2);
		
		return orthogonalVector;
	}
	
	/**
	 * Returns true if the input vector is normalized (length = 1)
	 * @param v		input vector
	 * @return true if the input vector is normalized (length = 1)
	 */
	public static boolean isNormalized(Vector3f v) {
		return isNormalized(v, 0.01f);
	}
	
	/**
	 * Returns true if the input vector is normalized (length = 1)
	 * @param v		input vector
	 * @param eps	tolerance
	 * @return true if the input vector is normalized (length = 1)
	 */
	public static boolean isNormalized(Vector3f v, float eps) {
		float val = v.length()-1.0f;
		return (-eps<val & val<eps);
	}

	/**
	 * Create a coordinate system linked to the sensor/emitter (z=direction)
	 * @param direction			direction of the system
	 * @return coordinate system
	 */
	public static Vector3f[] computeCoordinateSystem(Vector3f direction) {

		Vector3f x = new Vector3f(direction);
		x.normalize ();

		/*
		x : direction of the view
		y : always horizontal
		z : x^y
		*/
		
		Vector3f y = new Vector3f(x.y, -x.x, 0f);
		if (x.x*x.x+x.y+x.y==0)
			y = new Vector3f(0f,1f,0f);
		else
			y.normalize ();

		Vector3f z = new Vector3f();
		z.cross (x, y);

		Vector3f[] coordinateSystem = new Vector3f[3];
		coordinateSystem[0] = y;
		coordinateSystem[1] = z;
		coordinateSystem[2] = x;

		return coordinateSystem;

	}

	
}