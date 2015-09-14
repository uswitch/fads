(ns fads.core
  (:require [clj-facebook-graph.client :as fb]
            [clj-facebook-graph.auth :as fba]))

(defn account-insights [account-id params]
  (fb/get ["v2.4" (str "act_" account-id) "insights"]
          {:query-params params}))

(defn insights [token account-id]
  (fba/with-facebook-auth {:access-token token}
    (let [params {:level "adgroup"
                  :date_preset "yesterday"
                  :fields "adgroup_name,campaign_name,clicks,spend,impressions"}]
      (loop [all      nil
             response (account-insights account-id params)]
        (let [{:keys [data paging]} (:body response)
              after                 (get-in paging [:cursors :after])]
          (if (nil? after)
            all
            (recur (concat all data)
                   (account-insights account-id (assoc params :after after)))))))))
