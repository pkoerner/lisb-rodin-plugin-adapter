(ns lisb-rodin-plugin-adapter.core
  (:require [lisb.prob.animator :refer [api injector eval-formula']]
            [lisb.translation.eventb.util :as util]
            [lisb.translation.irtools :as irtools]
            [lisb.translation.eventb.irtools :as irt])
  (:gen-class
    :name de.hhu.stups.lisb.RodinPluginAdapter
    :methods [^{:static true} [getStateSpace [String] de.prob.statespace.StateSpace]
              ;; File -> StateSpace (loaded model)

              ^{:static true} [getIR [StateSpace String] java.util.Map]
              ;; StateSpace + MachineName / ContextName -> IR

              ^{:static true} [getLabeledPredicates [java.util.Map] java.util.Map]
              ;; IR -> (Operation -> (Label -> Predicate)) 

              ^{:static true} [getOpenIdentifiers [java.util.Map java.util.Map] java.util.List] 
              ;; IR + Predicate / Action -> List of variable names

              ^{:static true} [evaluatePredicate [de.prob.statespace.StateSpace java.util.Map java.util.Map] Boolean]
              ;; IR + Variable Bindings -> Boolean

              ^{:static true} [evaluateAction [de.prob.statespace.StateSpace java.util.Map java.util.Map] java.util.Map]
              ;; IR + Variable Bindings -> Variable Bindings of next State

               ]))

(defn -getStateSpace [file]
  ;; TODO: setup constants if possible?
  (.eventb_load api file))

(defn -getIR [statespace mch-or-ctx-name]
  (let [lisb (util/prob->lisb (.getModel statespace))
        full-ir (util/lisb->ir lisb)
        mch-or-ctx-ir (first (filter #(= (name (:name %)) mch-or-ctx-name) full-ir))]
    ;; TODO: verify all information is contained
    mch-or-ctx-ir))

(defn -getLabeledPredicates [ir]
  (irt/extract-labels ir))

(defn -getOpenIdentifiers [ir pred-or-action]
  ;; TODO: remove constants
  (irtools/find-identifiers pred-or-action)
  
  ;; TODO: collect all identifiers which are not local or constants
  )

(defn -evaluatePredicate [ir bindings]
  ()
  nil)

(defn -evaluateAction [ir bindings]
  nil)

(comment
  (def statespace (-getStateSpace "/home/philipp/tmp/rodin/workspace/NewProject/Clock.bum"))
  (def ir (-getIR statespace "Clock"))
  (def preds (-getLabeledPredicates ir) )


  (def statespace (.eventb_load api ))
  statespace
  (filter #(= (name (:name %)) "ClockDeepInstance") (first (util/lisb->ir (util/prob->lisb (.getModel statespace)))))
  (require 'lisb.translation.lisb2ir)
  (eval-formula' statespace (lisb.translation.util/ir->ast (lisb.translation.lisb2ir/b= :mm 1)))
  )
