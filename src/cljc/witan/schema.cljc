(ns witan.schema
  (:require [schema.core :as s]
            [schema-contrib.core :as sc]))

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

(def IdType
  s/Uuid)

(def DateTimeType
  sc/ISO-Date-Time)

(def User
  "User"
  {(s/required-key :id)   IdType
   (s/required-key :name) s/Str}) ;; TODO add groups

(def ModelPropertyType
  "Types that the model properties can be"
  (s/enum
   :text
   :number
   :dropdown))

(def ModelProperty
  "Properties that a model can expose"
  {(s/required-key :id)      IdType
   (s/required-key :name)    s/Str
   (s/required-key :type)    ModelPropertyType
   (s/optional-key :context) s/Any}) ;; varies depending on the type

(def ModelPropertyValue
  "A model property and value binding"
  {(s/required-key :property-id) IdType
   (s/required-key :value) s/Any}) ;; varies depending on the ModelProperty type

(def Model
  "Models are the center-piece of a Forecast"
  {(s/required-key :model-id)   IdType
   (s/required-key :name)       s/Str
   (s/required-key :owner)      IdType
   (s/required-key :version)    s/Int
   (s/optional-key :properties) [ModelProperty]})

(def DataItem
  "A data item"
  {(s/required-key :id)        IdType
   (s/required-key :name)      s/Str
   (s/required-key :publisher) IdType ;; a user or org
   (s/optional-key :version)   s/Int})

(def DataItemEntry
  "Used to isolate a data item ID"
  {(s/required-key :data-item) DataItem})

(def ModelInputCategory
  "Inputs into the model"
  {(s/required-key :id)   IdType
   (s/required-key :name) s/Str})

(def ModelOutputCategory
  "Outputs from the model"
  {(s/required-key :id)   IdType
   (s/required-key :name) s/Str})

(def ModelInput
  "An input category with a data item"
  {ModelInputCategory (s/maybe DataItemEntry)})

(def ModelOutput
  "An output category with a data item"
  {ModelOutputCategory (s/maybe (s/cond-pre [DataItemEntry] DataItemEntry))})

(def ModelInfo
  "More in-depth information about a Model"
  {(s/required-key :model)       Model
   (s/required-key :inputs)     [ModelInput]
   (s/required-key :outputs)    [ModelOutput]
   (s/optional-key :properties) [ModelPropertyValue]})

(def Tag
  "A tag is an annotated symlink to a forecast id and version"
  {(s/required-key :id)   IdType
   (s/required-key :name) s/Str
   (s/optional-key :description) s/Str
   (s/required-key :forecast-id) IdType
   (s/required-key :forecast-version) s/Int})

(def Forecast
  "Forecast"
  {(s/required-key :id)            IdType
   (s/required-key :name)          s/Str
   (s/required-key :owner)         IdType
   (s/required-key :series-id)     IdType
   (s/required-key :version)       s/Int
   (s/required-key :created)       DateTimeType
   (s/required-key :in-progress?)  s/Bool
   (s/required-key :descendant-id) (s/maybe IdType)
   (s/optional-key :description)   s/Str
   (s/optional-key :tag)           Tag})

(def ForecastInfo
  "Forecast in-depth"
  {(s/required-key :forecast)      Forecast
   (s/required-key :model)         ModelInfo})

(def ShareRequest
  "A request to adjust sharing rules for a tag"
  {(s/optional-key :add)     [sc/Email]
   (s/optional-key :message)  s/Str
   (s/optional-key :remove)  [sc/Email]})
