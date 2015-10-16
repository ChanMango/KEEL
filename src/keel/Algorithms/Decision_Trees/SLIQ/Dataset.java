/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

package keel.Algorithms.Decision_Trees.SLIQ;
import java.util.*;

import keel.Dataset.*;


/** 
 * Clase que representa un dataset o conjunto de datos
 */

public class Dataset {
	/** Nombre del dataset. */
	protected String name = "";
	
	/** Atributos que contiene. */
	protected Vector attributes;
	
	/** Conjuntos de elementos. */
	protected Vector itemsets;	
		
	/** indice del atributo de clase. */
	protected int classIndex;
	
	/** Keel dataset InstanceSet **/
	protected InstanceSet IS;

	/** MÃ©todo encargado de leer el archivo .dat que contiene la informaciÃ³n del dataset.
	 * 
	 * @param name 		El objeto lector en el que serÃ¡n leidos los conjuntos de elementos.
	 * @param train     Indica si el fichero es de entrenamiento
	 */
  	public Dataset(String name, boolean train) {
 		try {
            // Crea el conjunto de instancias
            IS = new InstanceSet();
            // Lee los conjuntos de elementos.
            IS.readSet(name,train);
		} catch(DatasetException e) {
			System.out.println("Error al leer instancias del dataset");
			e.printStackTrace();
			System.exit(-1);
		} catch(HeaderFormatException e) {
			System.out.println("Error al leer instancias del dataset");
			e.printStackTrace();
			System.exit(-1);
		}
		
		// Almacenar los atributos del dataset
		readHeader();

  		itemsets = new Vector(IS.getNumInstances());
  		
  		// Leer todos los conjuntos de elementos
  		getItemsetFull();  		  		
	}
 
	/** Constructor que obtiene una copia de un dataset.
	 * 
	 * @param dataset		El dataset a copiar.
	 */
  	public Dataset(Dataset dataset) {
  		this(dataset, dataset.numItemsets());
  		dataset.copyItemsets(0, this, dataset.numItemsets());
	}
  	
  	
  	/** Constructor para copiar todos los atributos de otro dataset excepto los conjuntos de elementos.
  	 * 
  	 * @param dataset		El dataset a copiar.
  	 * @param capacity		El numero de conjuntos de elementos.
  	 */
  	public Dataset(Dataset dataset, int capacity)  {
  		if(capacity < 0) 
  			capacity = 0;
    
  		classIndex = dataset.classIndex;
  		name = dataset.getName();
  		attributes = dataset.attributes;
  		itemsets = new Vector(capacity);
	}

  	/** MÃ©todo para almacenar la cabecera de un archivo de datos.
  	 * 
   	 */ 
  	private void readHeader()  {
  		String attributeName;
  		Vector attributeValues;
  		int i;

		name = Attributes.getRelationName();
		
  		// Vectores para almacenar temporalmente la informacion.
  		attributes = new Vector();
 
  		keel.Dataset.Attribute at;
  		
  		// almacenar atributo, entradas y salidas de la cabecera
  		for (int j=0; j<Attributes.getNumAttributes(); j++) {
  			at=Attributes.getAttribute(j);
  			attributeName = at.getName();
  			
  			// Comprobar si es de tipo real
  			if(at.getType()==2) {
  			 float min = (float) at.getMinAttribute();
  			 float max = (float) at.getMinAttribute();
  			 attributes.addElement(new Attribute(attributeName, j));
 			 Attribute att = (Attribute)attributes.elementAt(j);
			 att.setRange(min, max);  	
			 att.activate();
  			} else {
             // Comprobar si el tipo es entero
  			 if(at.getType()==1) {	
  			 	int min = (int) at.getMinAttribute();
  			 	int max = (int) at.getMinAttribute();  			 	  			 	  			 	
  			 	attributes.addElement(new Attribute(attributeName, j));
  			 	Attribute att = (Attribute)attributes.elementAt(j);
  			 	att.setRange(min, max);  	
  			 	att.activate();
  			 } else { // es discreto
  			 	attributeValues = new Vector();
  			 	for(int k=0; k<at.getNumNominalValues();k++) {
  			 		attributeValues.addElement(at.getNominalValue(k));
  			 	}
  			 	attributes.addElement(new Attribute(attributeName, attributeValues, j));
  			 	Attribute att = (Attribute)attributes.elementAt(j);
  			 	att.activate();
  			 }  			 	
  			}  			 		
  		} // for
  			
  		// Establecer el indice de la clase de salida
  		classIndex = Attributes.getNumAttributes() - 1;  				
	}

  	
  	
  	/** MÃ©todo para leer un conjunto de elementos y aÃ±adirlo al dataset
  	 * 
  	 * @return					True si se ha leÃ­do el conjunto de elementos sin problemas.
  	 * 
  	 */
  	private boolean getItemsetFull()  {
   		// rellenar el conjunto de elementos
  		for(int j=0; j<IS.getNumInstances(); j++) {
  	  		double[] itemset = new double[Attributes.getNumAttributes()];

  			// Obtener valores para todos los atributos de entrada.
  			for(int i=0; i<Attributes.getInputNumAttributes(); i++) {
  				// comprobar el tipo y si es nulo
  				if(IS.getInstance(j).getInputMissingValues(i))
  					itemset[i] = Itemset.getMissingValue();
  				else {
  					if(Attributes.getInputAttribute(i).getType()==0) { // nominal
  						for(int k=0; k<Attributes.getAttribute(i).getNumNominalValues();k++)	
  							if(Attributes.getAttribute(i).getNominalValue(k).equals(IS.getInstance(j).getInputNominalValues(i)))
  								itemset[i]=(double)k;	
  					} else { // real y entero
  						itemset[i]=IS.getInstance(j).getInputRealValues(i);  				    				
  					}
  				} // else  				            
			} //for
  	
  			// Obtener valores para el atributo de salida.
  			int i=Attributes.getInputNumAttributes();
  			
  			// comprobar el tipo y si es nulo
            if(IS.getInstance(j).getOutputMissingValues(0))
                itemset[i] = Itemset.getMissingValue();
            else {
                if(Attributes.getOutputAttribute(0).getType()==0) { //nominal
                    for(int k=0; k<Attributes.getOutputAttribute(0).getNumNominalValues();k++)
                        if(Attributes.getOutputAttribute(0).getNominalValue(k).equals(IS.getInstance(j).getOutputNominalValues(0)))
                            itemset[i]=(double)k;
                } else { // real y entero
                    itemset[i]=IS.getInstance(j).getOutputRealValues(0);
                }
            } // else

            // Agregar el conjunto de elementos al dataset
            addItemset(new Itemset(1, itemset));
  			
		} // for
  		return true;
	}

	/** MÃ©todo para agregar un conjunto de elementos al dataset.
	 * 
	 * @param itemset	El conjunto de elementos a aÃ±adir.
	 */
  	public final void addItemset(Itemset itemset) {
  		Itemset newItemset = (Itemset)itemset.copy();
 		
  		newItemset.setDataset(this);
  		itemsets.addElement(newItemset);  		  	  		
	}

  	/** Devuelve el nombre del dataset.
  	 * 
  	 */
  	public String getName() {
  		return name;
  	}

  	/** Devuelve el atributo correspondiente a un cierto Ã­ndice.
  	 * 
  	 * @param index		El Ã­ndice del atributo.
  	 */
	public final Attribute getAttribute(int index) {
  		return (Attribute) attributes.elementAt(index);
	}

  	/** Devuelve el atributo que tiene un cierto nombre.
  	 * 
  	 * @param name	El nombre del atributo.
  	 */
	public final Attribute getAttribute(String name) {
  		for(int i=0; i<attributes.size(); i++)
  			if(((Attribute)attributes.elementAt(i)).name().equalsIgnoreCase(name)) 
  				return (Attribute) attributes.elementAt(i);
  			
  		return null;
	}

	/** Devuelve el atributo de clase.
	 * 
	 */
	public final Attribute getClassAttribute() {
  		if(classIndex < 0) {
  			System.err.println("indice de clase incorrecto:"+classIndex);
  			return null;
  		}
  		return getAttribute(classIndex);
	}

	/** Devuelve el Ã­ndice del atributo de clase.
	 * 
	 */
	public final int getClassIndex() {
  		return classIndex;
	}
 
	/** Devuelve el nÃºmero de atributos.
	 * 
	 */
	public final int numAttributes() {
  		return attributes.size();
	}

	/** Devuelve el nÃºmero de posibles valores del atributo de clase.
	 * 
	 */
	public final int numClasses() {
  		if (classIndex < 0) {
  			System.err.println("indice de clase erroneo:"+classIndex);
  			return -1;
  		}
   		return getClassAttribute().numValues();
	}

	/** Devuelve el nÃºmero de conjuntos de datos.
	 * 
	 */
	public final int numItemsets() {
  		return itemsets.size();
	}

  	/** MÃ©todo que elimina el conjunto de elementos correspondiente a un Ã­ndice.
  	 * 
  	 * @param index 	indice del conjunto de datos a borrar.
  	 */
  	public final void delete(int index) {
  		itemsets.removeElementAt(index);
	}

	/** MÃ©todo para eliminar todos los atributos en los que falten valores.
	 * 
	 * @param attIndex		indice del atributo.
	 */
  	public final void deleteWithMissing(int attIndex) {
  		Vector newItemsets = new Vector(numItemsets());

  		for(int i=0; i<numItemsets(); i++)
  			if(!itemset(i).isMissing(attIndex))
  				newItemsets.addElement(itemset(i));

  		itemsets = newItemsets;
	}

	/** Enumera todos los atributos.
	 * 
	 * @return Una enumeraciÃ³n que contiene todos los atributos.
	 */
	public Enumeration enumerateAttributes()  {
  		Vector help = new Vector(attributes.size() - 1);
  		
  		for(int i=0; i<attributes.size(); i++)
  			if(i != classIndex)
  				help.addElement(attributes.elementAt(i));
  		
  		return help.elements();
	}

	/** Enumera todos los conjuntos de elementos.
	 * 
	 * @return	Una enumeraciÃ³n que contiene todos los conjutnos de elementos.
	 */
	public final Enumeration enumerateItemsets() {
  		return itemsets.elements();
	}

	/** Devuelve el conjunto de elementos de una cierta posiciÃ³n.
	 * 
	 * @param index	El Ã­ndice del conjunto de elementos.
	 */	
	public final Itemset itemset(int index) {
  		return (Itemset)itemsets.elementAt(index);
	}

	/** Devuelve el Ãºltimo conjunto de elementos.
	 * 
	 */
	public final Itemset lastItemset() {
  		return (Itemset)itemsets.lastElement();
	}

	
  	/** MÃ©todo para agregar las instancias de un conjunto al final de otro.
  	 * 
  	 * @param from	El Ã­ndice del primero a copiar.
  	 * @param dest	El dataset al que van a copiarse los conjuntos de elementos.
  	 * @param num	NÃºmero de conjuntos de elementos a copiar.
  	 */
  	private void copyItemsets(int from, Dataset dest, int num) {
	    for(int i=0; i<num; i++)
	      dest.addItemset(itemset(from + i));
    }

  	/** MÃ©todo que calcula la suma de los pesos de todos los conjuntos de elementos.
  	 * 
  	 * @return	El peso de todos los conjuntos de elementos.
  	 */
  	public final double sumOfWeights() {
  		double sum = 0;

  		for(int i=0; i<numItemsets(); i++)
  			sum += itemset( i ).getWeight();

  		return sum;
	}

  	/** MÃ©todo para ordenar el dataset en base a un atributo dado.
  	 * 
  	 * @param attIndex	Ã­ndice del atributo.
  	 */
  	public final void sort(int attIndex)  {
  		int i, j;

  		// Llevar al final todo el dataset con datos ausentes
  		j = numItemsets() - 1;
  		i = 0;
    
  		while(i <= j) {
  			if(itemset(j).isMissing(attIndex))
  				j--;
  			else {
  				if(itemset(i).isMissing(attIndex))  {
  					swap(i, j);
  					j--;
  				}
  				i++;
  			}
  		}
    
  		quickSort(attIndex, 0, j);
	}

  	/** MÃ©todo que implementa el algoritmo quicksort.
  	 * 
  	 * @param attIndex		indice del atributo utilizado para ordenar.
  	 * @param lo0			Valor mÃ­nimo.
  	 * @param hi0			Valor mÃ¡ximo.
  	 */
  	private void quickSort(int attIndex, int lo0, int hi0) {
  		int lo = lo0, hi = hi0;
  		double mid, midPlus, midMinus;
    
  		if (hi0 > lo0) {
  			// Arbitrarily establishing partition element as the 
  			// midpoint of the array.
  			mid = itemset((lo0 + hi0 ) / 2).getValue(attIndex);
  			midPlus = mid + 1e-6;
  			midMinus = mid - 1e-6;

  			// loop through the array until indices cross
  			while(lo <= hi) {
  				// find the first element that is greater than or equal to 
  				// the partition element starting from the left Index.
  				while((itemset(lo).getValue(attIndex) < midMinus) && (lo < hi0))
  					++lo;
	
  				// find an element that is smaller than or equal to
  				// the partition element starting from the right Index.
  				while ((itemset(hi).getValue(attIndex)  > midPlus) && (hi > lo0))
  					--hi;

  				// if the indexes have not crossed, swap
  				if(lo <= hi) {
  					swap( lo,hi );
  					++lo;
  					--hi;
  				}
  			}

  			// If the right index has not reached the left side of array
  			// must now sort the left partition.
  			if(lo0 < hi)
  				quickSort(attIndex, lo0, hi);

  			// If the left index has not reached the right side of array
  			// must now sort the right partition.
  			if(lo < hi0)
  				quickSort(attIndex, lo, hi0);
  		}
	}

  	/** Function to swap two itemsets. 
  	 * 
  	 * @param i		The first itemset.
  	 * @param j		The second itemset.
  	 */
  	private void swap(int i, int j) {
        Object help = itemsets.elementAt( i );

        itemsets.insertElementAt(itemsets.elementAt(j), i);
        itemsets.removeElementAt(i + 1);
        itemsets.insertElementAt(help, j);
        itemsets.removeElementAt(j + 1);
	}
  	


}
