(ns lisb-rodin-plugin-adapter.core
  (:require [lisb.translation.eventb.util]
            [lisb.translation.eventb.irtools])
  (:gen-class
    :name de.hhu.stups.lisb.RodinPluginAdapter
    :methods [^{:static true} [loadModel [java.io.File String] java.util.Map]
              ;; File + MachineName / ContextName -> IR or rather [StateSpace, IR] ??
              ;; TODO: probably needs static context as well

              ^{:static true} [getLabeledPredicates [java.util.Map] java.util.Map]
              ;; IR -> (Operation -> (Label -> Predicate)) 

              ^{:static true} [evaluatePredicate [java.util.Map java.util.Map] Boolean]
              ;; IR + Variable Bindings -> Boolean

              ^{:static true} [evaluateAction [java.util.Map java.util.Map] java.util.Map]
              ;; IR + Variable Bindings -> Variable Bindings of next State
               ]))

(defn -loadModel [file namey]
  nil)

(defn -getLabeledPredicates [ir]
  nil)

(defn -evaluatePredicate [ir bindings]
  nil)

(defn -evaluateAction [ir bindings]
  nil)
