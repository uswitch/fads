(ns fads.core
  (:require [clj-facebook-graph.client :as fb]
            [clj-facebook-graph.auth :as fba]))

(defn insights [token account-id]
  (fba/with-facebook-auth {:access-token token}
    (fb/get ["v2.4" (str "act_" account-id) "insights"]
            {:query-params {:level "adgroup"
                            :date_preset "yesterday"
                            :fields "adgroup_name,campaign_name,clicks,spend,impressions"}})))
