/* --------------------------------------------------------------------------
Mise en correspondance de points d'interet detectes dans deux images
Copyright (C) 2010, 2011  Universite Lille 1

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
-------------------------------------------------------------------------- */

/* --------------------------------------------------------------------------
Inclure les fichiers d'entete
-------------------------------------------------------------------------- */
#include <stdio.h>
#include <iostream>
#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>
#include <opencv2/highgui/highgui.hpp>
using namespace cv;
using namespace std;
#include "glue.hpp"
#include "Elliot-Vanegue.hpp"

// -----------------------------------------------------------------------
/// \brief Detecte les coins.
///
/// @param mImage: pointeur vers la structure image openCV
/// @param iMaxCorners: nombre maximum de coins detectes
/// @return matrice des coins
// -----------------------------------------------------------------------
Mat iviDetectCorners(const Mat& mImage,
                     int iMaxCorners) {
    // A modifier !
    vector<Point2f> corners;

    goodFeaturesToTrack( mImage, corners, iMaxCorners, 0.01,
               10, Mat(), 3, false, 0.04 );

    int sizeCorners = corners.size();
    Mat mCorners = Mat(3,sizeCorners,CV_64F);

    for(int i=0; i<sizeCorners; i++) {
        mCorners.at<double>(0, i) = corners[i].x;
        mCorners.at<double>(1, i) = corners[i].y;
        mCorners.at<double>(2, i) = 1.0;
    }

    // Retour de la matrice
    return mCorners;
}

// -----------------------------------------------------------------------
/// \brief Initialise une matrice de produit vectoriel.
///
/// @param v: vecteur colonne (3 coordonnees)
/// @return matrice de produit vectoriel
// -----------------------------------------------------------------------
Mat iviVectorProductMatrix(const Mat& v) {
    // A modifier !
    //Mat mVectorProduct = Mat::eye(3, 3, CV_64F);
    Mat mVectorProduct = (Mat_<double>(3,3) <<
            0.0, -v.at<double>(0,2), v.at<double>(0,1),
            v.at<double>(0,2), 0.0, -v.at<double>(0,0),
            -v.at<double>(0,1), v.at<double>(0,0), 0.0);
    // Retour de la matrice
    return mVectorProduct;
}

// -----------------------------------------------------------------------
/// \brief Initialise et calcule la matrice fondamentale.
///
/// @param mLeftIntrinsic: matrice intrinseque de la camera gauche
/// @param mLeftExtrinsic: matrice extrinseque de la camera gauche
/// @param mRightIntrinsic: matrice intrinseque de la camera droite
/// @param mRightExtrinsic: matrice extrinseque de la camera droite
/// @return matrice fondamentale
// -----------------------------------------------------------------------
Mat iviFundamentalMatrix(const Mat& mLeftIntrinsic,
                         const Mat& mLeftExtrinsic,
                         const Mat& mRightIntrinsic,
                         const Mat& mRightExtrinsic) {
    // A modifier !
    // Doit utiliser la fonction iviVectorProductMatrix
    Mat mFundamental = Mat::eye(3, 3, CV_64F);
    Mat tmp = (Mat_<double>(3,4) <<
        1.0, 0.0, 0.0, 0.0,
        0.0, 1.0, 0.0, 0.0,
        0.0, 0.0, 1.0, 0.0
        );
    Mat P1 = mLeftIntrinsic * tmp * mLeftExtrinsic;
    Mat P2 = mRightIntrinsic * tmp * mRightExtrinsic;

    Mat O = mLeftExtrinsic.inv();
    Mat O1 = (Mat_<double>(4,1) <<
        O.at<double>(3),
        O.at<double>(7),
        O.at<double>(11),
        O.at<double>(15)
        );

    Mat Hpi = P2 * P1.inv(DECOMP_SVD);
    mFundamental = iviVectorProductMatrix(P2*O1) * Hpi;

    // Retour de la matrice fondamentale
    return mFundamental;
}

// -----------------------------------------------------------------------
/// \brief Initialise et calcule la matrice des distances entres les
/// points de paires candidates a la correspondance.
///
/// @param mLeftCorners: liste des points 2D image gauche
/// @param mRightCorners: liste des points 2D image droite
/// @param mFundamental: matrice fondamentale
/// @return matrice des distances entre points des paires
// -----------------------------------------------------------------------
Mat iviDistancesMatrix(const Mat& m2DLeftCorners,
                       const Mat& m2DRightCorners,
                       const Mat& mFundamental) {
    // A modifier !
    Mat D2 = mFundamental * m2DLeftCorners;
    Mat Ft = Mat();
    transpose(mFundamental, Ft);
    Mat D1 = Ft * m2DRightCorners;

    int size = D1.cols;
    Mat mDistances = Mat::eye(size, size, CV_64F);
    
    for(int i=0; i<size; i++) {
        for(int j=0; j<size; j++) {
            double num = fabs(D1.at<double>(i,0)*m2DLeftCorners.at<double>(i,0)+D1.at<double>(i,1)*m2DLeftCorners.at<double>(i,1)+
                D1.at<double>(i,2));
            double denom = sqrt(D1.at<double>(i,0)*D1.at<double>(i,0)+D1.at<double>(i,1)*D1.at<double>(i,1));
            double d1 = num/denom;

            num = fabs(D2.at<double>(j,0)*m2DRightCorners.at<double>(j,0)+D2.at<double>(j,1)*m2DRightCorners.at<double>(j,1)+
                D2.at<double>(j,2));
            denom = sqrt(D2.at<double>(j,0)*D2.at<double>(j,0)+D2.at<double>(j,1)*D2.at<double>(j,1));
            double d2 = num/denom;
            double distance = d1 + d2;
            mDistances.at<double>(i,j) = distance;
        }
    }

    // Retour de la matrice fondamentale
    return mDistances;
}

// -----------------------------------------------------------------------
/// \brief Initialise et calcule les indices des points homologues.
///
/// @param mDistances: matrice des distances
/// @param fMaxDistance: distance maximale autorisant une association
/// @param mRightHomologous: liste des correspondants des points gauche
/// @param mLeftHomologous: liste des correspondants des points droite
/// @return rien
// -----------------------------------------------------------------------
void iviMarkAssociations(const Mat& mDistances,
                         double dMaxDistance,
                         Mat& mRightHomologous,
                         Mat& mLeftHomologous) {
    // A modifier !
    int size = mDistances.cols;
    mRightHomologous = Mat::eye(1, size, CV_64F);
    mLeftHomologous = Mat::eye(1, size, CV_64F);
    double minValueR = std::numeric_limits<double>::infinity();
    int minIndexR = size+1;
    double minValueL = std::numeric_limits<double>::infinity();
    int minIndexL = size+1;

    for(int i=0; i<size; i++) {
        for(int j=0; j<size; j++) {
            double distanceR = mDistances.at<double>(i,j);
            if(distanceR < dMaxDistance && distanceR < minValueR) {
                minValueR = distanceR;
                minIndexR = j;
            }
            double distanceL = mDistances.at<double>(j,i);
            if(distanceL < dMaxDistance && distanceL < minValueL) {
                minValueL = distanceL;
                minIndexL = j;
            }
        }
        if(minValueR < std::numeric_limits<double>::infinity()) 
            mRightHomologous.at<double>(0,i) = minIndexR;
        else
            mRightHomologous.at<double>(0,i) = -1;
        if(minValueL < std::numeric_limits<double>::infinity()) 
            mLeftHomologous.at<double>(0,i) = minIndexL;
        else
            mLeftHomologous.at<double>(0,i) = -1;
    }        
    cout << mRightHomologous << endl;
    cout << mLeftHomologous << endl;
}
