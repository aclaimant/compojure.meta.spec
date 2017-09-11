(ns aclaimant.compojure.meta.spec
  (:require
    [aclaimant.compojure.meta :as compojure.meta]
    [clojure.spec.alpha :as s]))

(defn ^:private assert-conforms [spec v]
  (s/assert spec v)
  (s/conform spec v))

(defn wrap-request-spec
  "Meta-middleware to use :request-spec route metadata to enforce a spec on (:params request)"
  [handler]
  (fn [{:keys [params metadata] :as req}]
    (handler
      (if-let [request-spec (:request-spec metadata)]
        (assoc req
               :params (assert-conforms request-spec params)
               :unconformed-params params)
        req))))

(defn wrap-response-spec
  "Meta-middleware to use :response-spec route metadata to enforce a spec on route response"
  [handler]
  (fn [{:keys [params metadata] :as req}]
    (let [response (handler req)]
      (if-let [response-spec (:response-spec metadata)]
        (assert-conforms response-spec response)
        response))))

(defn wrap-specs
  "Ring middleware that adds meta-middleware for enforcing request and response
   specs. You must use aclimant.compojure.meta/* when defining the route with
   :request-spec and/or :response-spec"
  [handler]
  (compojure.meta/wrap-meta-middleware handler wrap-request-spec wrap-response-spec))
