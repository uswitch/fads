(ns fads.core
  (:require [clj-oauth2.client :as oauth]
            [slingshot.slingshot :as sling]
            [cemerick.url :refer (url url-encode)]
            [clojure.reflect :as r]
            [clojure.data.json :as json]
            [clj-time.format :as tf]
            [clojure.string :as s]))

(defn graph-url [access-token paths query-params]
  (let [query (assoc query-params :access_token access-token)
        url   (assoc (apply url "https://graph.facebook.com/v2.5" paths) :query query)]
    url))

(defn fetch [req]
  (let [{:keys [body status] :as resp} (oauth/get (str req) {})]
    (if (= 200 status)
      (update-in resp
                 [:body]
                 #(json/read-str % :key-fn keyword))
      resp)))

(defn insights
  "Retrieves adgroup level statistics, will follow pagination links to
  return a sequence of all data. Field names taken from:
  https://developers.facebook.com/docs/marketing-api/insights/v2.5"
  [token account-id {:keys [since until after level fields]
                     :or   {level "adgroup"
                            fields ["adgroup_name" "campaign_name" "campaign_id" "clicks" "spend" "impressions"]}}]
  (let [base-query {:time_increment 1
                    :after          after
                    :time_range     (json/write-str {:since (tf/unparse (tf/formatters :date) since)
                                                     :until (tf/unparse (tf/formatters :date) until)})}
        query      (assoc base-query :level level :fields (s/join "," fields))
        fetch-data (fn [query]
                     (-> (graph-url token [(str "act_" account-id) "insights"] query) (fetch) :body))]
    (loop [result   nil
           response (fetch-data query)]
      (let [{:keys [data paging]} response
            after                 (get-in paging [:cursors :after])]
        (if (nil? after)
          (concat result data)
          (recur (concat result data)
                 (fetch-data (assoc query :after after))))))))

(comment
  (def token "OAUTH2_TOKEN")
  (def account-id "ACCOUNT_ID")

  (insights token account-id {:since (from-now (days -7))
                              :until (today)}))
