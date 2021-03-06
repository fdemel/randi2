package de.randi2.model.criteria;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import de.randi2.model.AbstractDomainObject;
import de.randi2.model.criteria.constraints.AbstractConstraint;
import de.randi2.unsorted.ContraintViolatedException;
import de.randi2.utility.Randi2Error;

/**
 * This class maps the needed behaviour of a Trial subject. With the Classes
 * inherited from this class you can define anything you need, referring to the
 * properties a trials subject should have:
 * <ul>
 * <li>Properties that needs to be entered</li>
 * <li>Inclusion Criteria</li>
 * <li>Stratification</li>
 * </ul>
 * 
 */
@Entity
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@Table(name="Criterion")
public abstract class AbstractCriterion<V extends Serializable, C extends AbstractConstraint<V>> extends AbstractDomainObject {

	private static final long serialVersionUID = 6845807707883121147L;


	// The name of the criterion i.e. birthday
	public String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String description;

	@Transient
	protected List<V> configuredValues;
	
	@Transient
	public abstract List<V> getConfiguredValues();
	
	/**
	 * If the object represents an inclusion criteria, this field has the
	 * constraints.
	 */
	@OneToOne(targetEntity=AbstractConstraint.class, cascade=CascadeType.ALL)
	protected C inclusionCriterion;

	@OneToMany(targetEntity=AbstractConstraint.class, cascade=CascadeType.ALL)
	protected List<C> strata;

	@Transient
	public C getInclusionCriterion(){
		return inclusionCriterion;
	}

	public void setInclusionCriterion(C inclusionCriterion){
		this.inclusionCriterion = inclusionCriterion;
	}
	
	@Transient
	public abstract Class<C> getContstraintType();

	@Transient
	public List<C> getStrata(){
		return strata;
	}

	public void setStrata(List<C> strata){
		this.strata = strata;
	}
	
	public C stratify(V value) throws ContraintViolatedException{
		this.isValueCorrect(value);
		if(strata==null)
			return null;
		for(C stratum : strata){
			if(stratum.checkValue(value))
				return stratum;
		}
		throw new Randi2Error("Valid value could not be assigned to any stratum.");
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Transient
	public  boolean isInclusionCriterion(){
		return inclusionCriterion!=null;
	}

	public abstract void isValueCorrect(V value) throws ContraintViolatedException;

	public boolean checkValue(V value){
		try{
			isValueCorrect(value);
			return true;
		}
		catch(ContraintViolatedException e){
			return false;
		}
	}
}
