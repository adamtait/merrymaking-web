(ns com.tamandadam.merrymaking-web.mail
  (:require [com.tamandadam.merrymaking-web.log :refer [log log-error]])
  (:import
   (java.util Properties)
   (javax.mail Message MessagingException Session Transport)
   (javax.mail.internet AddressException InternetAddress MimeMessage)))

(defn ^InternetAddress email-address
  [address name]
  (InternetAddress. address name))

(defn source-email-address []
  (email-address
   "noreply@merrymaking-web.appspotmail.com"
   "adamandtam.com RSVP form"))

(defn send-email [^String dest-address subject message]
  (let [props (Properties.)
        session (Session/getDefaultInstance props)]
    (try
      (let [src-address (source-email-address)
            msg (doto
                    (MimeMessage. session)
                  (.setFrom src-address)
                  (.addRecipients javax.mail.Message$RecipientType/TO dest-address)
                  (.setSubject subject)
                  (.setText message))]
        (Transport/send msg))
      (catch AddressException e
        (log-error e "Unable to send mail"))
      (catch MessagingException e
        (log-error e "Unable to send mail")))))



(def dest-addresses
  ["adam@adamtait.com"
   "tamthaopham@gmail.com"])

(defn send-new-rsvp [from-name message]
  (doseq [addr dest-addresses]
    (send-email
     addr
     (str "New RSVP from " from-name)
     message)))
