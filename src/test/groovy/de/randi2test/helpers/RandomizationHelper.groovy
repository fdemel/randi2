package de.randi2test.helpers

import de.randi2.model.Trial as T
import de.randi2.model.TreatmentArm as TA
import de.randi2.model.randomization.*

import de.randi2.utility.Pair

/**
 *
 * @author jthoenes
 */
class RandomizationHelper {
	static createTrial(conf){
        def trial = new T()
        trial.randomizationConfiguration = conf
        
        trial
    }

    static createTrialWithArms(conf, armSizes){
        def trial = createTrial(conf)
        def treatmentArms = []
        armSizes.each { size ->
            def arm = InitializationHelper.createTreatmentArm(plannedSubjects : size)
            treatmentArms << arm
        }
        trial.treatmentArms = treatmentArms

        new Pair(trial, treatmentArms)
    }
}

