(ns aclaimant.compojure.meta.spec
  (:require
    [clojure.spec :as s]))

(defn ^:private assert-conforms [spec v]
  (s/assert spec v)
  (s/conform spec v))

(defn wrap-request-spec
  "Middleware to use :request-spec route metadata to enforce a spec on (:params request)"
  [handler]
  (fn [{:keys [params metadata] :as req}]
    (handler
      (if-let [request-spec (:request-spec metadata)]
        (assoc req
               :params (assert-conforms request-spec params)
               :unconformed-params params)
        req))))

(defn wrap-response-spec
  "Middleware to use :response-spec route metadata to enforce a spec on route response"
  [handler]
  (fn [{:keys [params metadata] :as req}]
    (let [response (handler req)]
      (if-let [response-spec (:response-spec metadata)]
        (assert-conforms response-spec response)
        response))))
