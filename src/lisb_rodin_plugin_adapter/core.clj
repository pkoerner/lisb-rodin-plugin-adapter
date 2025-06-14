(ns lisb-rodin-plugin-adapter.core
  (:require [lisb.prob.animator :refer [api injector eval-formula']]
            [lisb.translation.eventb.util :as util]
            [lisb.translation.util :as butil]
            [lisb.translation.irtools :as irtools]
            [lisb.translation.ir2ast]
            [lisb.translation.eventb.irtools :as irt])
  (:import [de.prob.animator.command FindStateCommand]
           [de.prob.animator.domainobjects ClassicalB]
           [de.prob.animator.domainobjects FormulaExpand]
           [java.util.concurrent TimeoutException])
  (:gen-class
    :name de.hhu.stups.lisb.RodinPluginAdapter
    :methods [^{:static true} [getStateSpace [String] de.prob.statespace.StateSpace]
              ;; File -> StateSpace (loaded model)

              ^{:static true} [getIR [de.prob.statespace.StateSpace String] java.util.Map]
              ;; StateSpace + MachineName / ContextName -> IR

              ^{:static true} [getLabeledPredicates [java.util.Map] java.util.Map]
              ;; IR -> (Operation -> (Label -> Predicate)) 

              ^{:static true} [getOpenIdentifiers [java.util.Map java.util.Map] java.util.List] 
              ;; IR + Predicate / Action -> List of variable names

              ^{:static true} [evaluatePredicate [de.prob.statespace.StateSpace java.util.Map java.util.Map] Boolean]
              ;; IR + Variable Bindings -> Boolean

              ^{:static true} [evaluateAction [de.prob.statespace.StateSpace java.util.Map java.util.Map] java.util.Map]
              ;; IR + Variable Bindings -> Variable Bindings of next State

              ^{:static true} [bexpr2ir [String] java.util.Map]
              ^{:static true} [bpred2ir [String] java.util.Map]

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

(defn -evaluatePredicate [statespace ir bindings]
  ;; TODO: this is basically the evaluation mechanism in lisb --- refactor!
  (let [formula (apply butil/band (map (fn [[id v]] (butil/b= (keyword id) (butil/b-expression->ir v))) bindings))
        formula-str (butil/ir->b formula)
        ; _ (println formula-str)
        fsc (FindStateCommand. statespace (ClassicalB. (butil/b-predicate->ast formula-str) FormulaExpand/EXPAND "") false)
        _ (.execute statespace fsc)
        newstate (.getDestination (.getOperation fsc))
        ;; include bindings in solution for post-variables
        ; _ (println (butil/ir->b (butil/band formula ir)))
        res (.eval newstate (butil/ir->b (butil/band formula ir)))
        res-val (.getValue res)]
    ; (def res res)
    ;; TODO: handle res
    (case res-val
          "FALSE" false
          "time_out" (throw (TimeoutException. "evaluating the prediate timed out"))
          "TRUE" true)))

(defn -getWrittenVariables [ir]
  (let [parts (partition 2 (:id-vals ir))]
    (map first parts)))

(defn -evaluateAction [statespace ir bindings]
  ;; TODO: handle all possible substitutions
  (let [parts (partition 2 (:id-vals ir))
        ids (map first parts)
        vs (map second parts)]
    (-evaluatePredicate statespace
                        (apply butil/band (map (fn [id v] (butil/b= (keyword (str "lisb__postsubst__" (name id))) v)) ids vs))
                        bindings)))

(defn -bexpr2ir [s] (butil/b-expression->ir s))
(defn -bpred2ir [s] (butil/b-predicate->ir s))

;; HACK: this should be moved to lisb (as probably most of the stuff above)
(defmethod lisb.translation.ir2ast/ir-node->ast-node :theorem [ir-node]
  (lisb.translation.ir2ast/ir-node->ast-node (:pred ir-node)))

(comment
  (def statespace (-getStateSpace "/home/philipp/tmp/rodin/workspace/NewProject/Clock.bum"))
  (def ir (-getIR statespace "Clock"))
  (def preds (-getLabeledPredicates ir) )


  (def statespace (.eventb_load api ))
  statespace
  (filter #(= (name (:name %)) "ClockDeepInstance") (first (util/lisb->ir (util/prob->lisb (.getModel statespace)))))
  (require 'lisb.translation.lisb2ir)
  (eval-formula' statespace (lisb.translation.util/ir->ast (lisb.translation.lisb2ir/b= :mm 1)))

  (def statespace (-getStateSpace "/home/philipp/tmp/drohnen-projekt/EventB_Model/Projekt/Drone_Emergency.bum"))
  (def ir (-getIR statespace "Drone_Emergency"))
  ir
  (def preds (-getLabeledPredicates ir) )
  (-getOpenIdentifiers nil (get-in preds ["take_off" ""]))
  (butil/ir->b (get-in preds ["INVARIANT" "type_emergency"]))

  (eval-formula' statespace (lisb.translation.util/ir->ast (lisb.translation.lisb2ir/b= :z 1)))

  (def fsc (FindStateCommand. statespace (ClassicalB. (butil/b-predicate->ast "z=1 & TAKE_OFF_DIST = 2 & Z_RAN = 1..2") FormulaExpand/EXPAND "") false))
  (.execute statespace fsc)
  (def newstate (.getDestination (.getOperation fsc)))
  (.eval newstate (butil/ir->b (get-in preds ["take_off" "z_ran"])))
  preds

  (-evaluatePredicate statespace (get-in preds ["take_off" "z_ran"]) {"z" "1", #_#_"TAKE_OFF_DIST" "2", "Z_RAN" "1..3"})
  (-evaluateAction statespace (get-in preds ["take_off" "estimante_z"]) {"z" "1", "TAKE_OFF_DIST" "2", "lisb__postsubst__z" "3"})


  (def statespace (-getStateSpace "/home/philipp/tmp/rodin/workspace/Counter/Counter.bum"))
  (def ir (-getIR statespace "Counter"))
  (def preds (-getLabeledPredicates ir))
  (-evaluatePredicate statespace (get-in preds ["INVARIANT" "inv1"]) {"count" "150"})

  res
  )

