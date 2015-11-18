// Une macro-squelette calculer OTSU.
// Version: 0.1
// Date: sept 2015
// Author: L. Macaire
 // par calcul de chaque intravalle
macro "otsu" {

	image = getImageID();

	W = getWidth();
	H = getHeight();

	run("Duplicate...", "title=binarisee");
	image_binaire = getImageID();

	getHistogram (level,histo,256);

	// affichage de l'histogramme
	/*for ( i =0 ; i<= 255; i++) {
		print ("histo[",level[i],"] =", histo[i]);
	}*/


	// valeur initiale de omega1 mu1 omega2 mu2
	omega1 = 0;
	somme1 = 0;
	omega2 = 0;
	somme2 = 0;
	omega3 = 0;
	somme3 = 0;

	mu1 = 0;
	mu2 = 0;
	mu3 = 0;

	sSomme1 = 0;
	sSomme2 = 0;
	sSomme3 = 0;

	sigma1 = 0;
	sigma2 = 0;
	sigma3 = 0;

	min_val = 99999999999999999999999;
	i_max = 255;
	intra = 0;

	//recherche du premier seuil
	for( k = 2; k < 255; k++) {

		//recherche du deuxieme seuil
		for( k2 = 3; k2 < 255; k2++) {

		///////// OMEGA
				omega1 = 0;
				somme1 = 0;
				omega2 = 0;
				somme2 = 0;
				omega3 = 0;
				somme3 = 0;

				//calcul de la probabilité qu un pixel appartienne
				//a la premiere classe
				for(t=0; t<k; t++) {
					omega1 = omega1 + histo[t];
				}

				//calcul de la probabilité qu un pixel appartienne
				//a la deuxieme classe
				for(t=k; t<k2; t++) {
					omega2 = omega2 + histo[t];
				}

				//calcul de la probabilité qu un pixel appartienne
				//a la troisieme classe
				for(t=k2; t<255; t++) {
					omega3 = omega3 + histo[t];
				}


		/////// MU
		    //calcul du nombre de pixel pour chaque classe
				for(t=0; t<k; t++) {
					somme1 = somme1 +histo[t]*t;
				}
				for(t=k; t<k2; t++) {
					somme2 = somme2 + histo[t]*t;
			  }
				for(t=k2; t<255; t++) {
					somme3 = somme3 + histo[t]*t;
			  }

				if (omega1 * omega2 * omega3 != 0) {

					  //calcul du centre de gravité de chaque classe
						mu1 = somme1 / omega1;
						mu2 = somme2 / omega2;
						mu3 = somme3 / omega3;
		        print ("k=",k);
		        print ("mu1=",mu1);
		        print ("mu2=",mu2);
						print ("mu3=",mu3);


		//////////// Sigma
						sSomme1 = 0;
						sSomme2 = 0;
						sSomme3 = 0;

						//calcul de la variance de chaque classe
						for(t=0; t<k; t++) {
						  sSomme1 = sSomme1 +(t-mu1)*(t-mu1)*histo[t];
						}
						for(t=k; t<k2; t++) {
							sSomme2 = sSomme2 +(t-mu2)*(t-mu2)*histo[t];
						}
						for(t=k2; t<255; t++) {
							sSomme3 = sSomme3 +(t-mu3)*(t-mu3)*histo[t];
						}
						sigma1 = sSomme1 / omega1;
						sigma2 = sSomme2 / omega2;
						sigma3 = sSomme3 / omega3;

						print ("sigma1=",sigma1);
						print ("sigma2=",sigma2);
						print ("sigma3=",sigma3);

						intra = omega1*sigma1 + omega2*sigma2 + omega3*sigma3;

						if(min_val > intra) {
							t1_min = k;
							t2_min = k2;
							min_val = intra;
						}

					} // if
				}
			} // for t

	selectImage(image_binaire);

	//modification de la valeur des pixels pour visiualiser
	//les trois classes
	for(i = 0; i < W; i++) {
		for(j = 0; j < H; j++) {
			if(getPixel(i,j) < t2_min) {
				if(getPixel(i,j) < t1_min) {
					setPixel(i, j, 0);
				} else {
					setPixel(i, j, 125);
				}
			} else {
				setPixel(i, j, 255);
			}
		}
	}
	print ("seuil t1=",t1_min);
	print ("seuil t2=",t2_min);

	Dialog.create("Fin");
	Dialog.addMessage(" Cliquer sur OK pour terminer le traitement sur la saturation");
	Dialog.show();
}
