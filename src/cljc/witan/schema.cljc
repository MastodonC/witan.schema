(ns witan.schema
  (require [schema.core :as s]))

(defn length-greater [l]
  (s/pred
   (fn [x]
     (> (count x) l))))

(defn matches [r]
  (s/pred
   (fn [s]
          (re-matches r s))))

(defn is-an-email []
  (s/pred
   (fn [s] (re-matches #".*@.*" s))))

(def LoginDetails
  "validation for /login"
  {(s/required-key :username) (s/both (length-greater 5) (is-an-email))
   (s/required-key :password) (length-greater 5)})
