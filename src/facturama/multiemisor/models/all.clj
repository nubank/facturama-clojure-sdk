(ns facturama.multiemisor.models.all
  (:require [common-core.schema :as csc]
            [schema.core :as s]
            [facturama.multiemisor.models.cfdi-field-types :as s-cft])
  (:import [java.time LocalDate LocalDateTime]))

;;;;;;;;;;;; Schemas / data model used by Facturama's Multiemisor API ;;;;;;;;;;;;
;; Docs at: https://apisandbox.facturama.mx/Docs-multi
;; Java SDK Data Model at: https://github.com/Facturama/facturama-java-sdk/tree/dev/src/main/java/com/Facturama/sdk_java/Models
;;
;; Note: all LocalDateTime's in this API are assumed to be zoned at America/Mexico_City

(s/defschema MexicanPostalCode (s/pred #(re-matches #"^\d{5}$" %)))
(s/defschema CURP (s/pred #(re-matches #"^[A-Z][AEIOUX][A-Z]{2}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[MH]([ABCMTZ]S|[BCJMOT]C|[CNPST]L|[GNQ]T|[GQS]R|C[MH]|[MY]N|[DH]G|NE|VZ|DF|SP)[BCDFGHJ-NP-TV-Z]{3}[0-9A-Z][0-9]$" %)))
(s/defschema RFC (s/pred #(re-matches #"^XEXX010101000|[A-Z&amp;Ñ]{3}[0-9]{2}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{2}[0-9A]$" %)))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=IssuerBindingModel
(s/defschema IssuerBindingModelName (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(def issuer-binding-model-skeleton {:fiscal-regime {:required true :schema s-cft/FiscalRegimeCode}
                                    :rfc           {:required true :schema RFC}
                                    :name          {:required true :schema IssuerBindingModelName}})
(s/defschema IssuerBindingModel (csc/loose-schema issuer-binding-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ReceiverBindingModel
(s/defschema ReceiverBindingModelName (s/pred #(re-matches #"^[^|]{1,300}$" %)))
(def receiver-binding-model-skeleton {:rfc                     {:required true :schema RFC}
                                      :name                    {:required true :schema ReceiverBindingModelName}
                                      :cfdi-use                {:required true :schema s-cft/CfdiUseCode}

                                      :id                      {:required false :schema s/Str}
                                      :tax-residence           {:required false :schema s/Str}
                                      :tax-registration-number {:required false :schema s/Str}})
(s/defschema ReceiverBindingModel (csc/loose-schema receiver-binding-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=TaxBindingModel
(s/defschema TaxBindingModelName (s/pred #(re-matches #"^IVA|ISR|IEPS|IVA RET|IVA Exento$" %)))
(def tax-binding-model-skeleton {:total        {:required true :schema s/Num}
                                 :name         {:required true :schema TaxBindingModelName}
                                 :rate         {:required true :schema s/Num}

                                 :base         {:required false :schema s/Num}
                                 :is-retention {:required false :schema s/Bool}
                                 :is-quota     {:required false :schema s/Bool}})
(s/defschema TaxBindingModel (csc/loose-schema tax-binding-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=TaxStampModel
(def tax-stamp-model-skeleton {:uuid            {:required true :schema s/Str}
                               :date            {:required true :schema LocalDateTime} ;; server generated stamping timestamp
                               :cfdi-sign       {:required true :schema s/Str}
                               :sat-cert-number {:required true :schema s/Str}
                               :sat-sign        {:required true :schema s/Str}
                               :rfc-prov-certif {:required true :schema RFC}})
(s/defschema TaxStampModel (csc/loose-schema tax-stamp-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=RelatedDocument
(s/defschema PaymentMethod (s/pred #(re-matches #"^PUE|PIP|PPD$" %)))
(def related-document-skeleton {:uuid                    {:required true :schema s/Str}
                                :payment-method          {:required true :schema PaymentMethod}

                                :serie                   {:required false :schema s/Str}
                                :folio                   {:required false :schema s/Str}
                                :currency                {:required false :schema s/Str}
                                :exchange-rate           {:required false :schema s/Num}
                                :partiality-number       {:required false :schema s/Int}
                                :previous-balance-amount {:required false :schema s/Num}
                                :amount-paid             {:required false :schema s/Num}})
(s/defschema RelatedDocument (csc/loose-schema related-document-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=PaymentBindingModel
(def payment-binding-skeleton {:related-documents                {:required true :schema [RelatedDocument]}
                               :taxes                            {:required true :schema [TaxBindingModel]}
                               :date                             {:required true :schema LocalDateTime}
                               :payment-form                     {:required true :schema s/Str}
                               :currency                         {:required true :schema s/Str}
                               :amount                           {:required true :schema s/Num}

                               :exchange-rate                    {:required false :schema s/Num}
                               :operation-number                 {:required false :schema s/Str}
                               :rfc-issuer-payer-account         {:required false :schema RFC}
                               :foreign-account-name-payer       {:required false :schema s/Str}
                               :payer-account                    {:required false :schema s/Str}
                               :rfc-receiver-beneficiary-account {:required false :schema RFC}
                               :beneficiary-account              {:required false :schema s/Str}
                               :expected-paid                    {:required false :schema s/Num}})
(s/defschema PaymentBindingModel (csc/loose-schema payment-binding-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ComplementModel
(def complement-model-skeleton {:tax-stamp {:required true :schema TaxStampModel}

                                :payments  {:required false :schema [PaymentBindingModel]}})
(s/defschema ComplementModel (csc/loose-schema complement-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Address
(s/defschema AddressStreet (s/pred #(re-matches #"^.{1,100}$" %)))
(s/defschema AddressExteriorNumber (s/pred #(re-matches #"^.{1,30}$" %)))
(s/defschema AddressInteriorNumber (s/pred #(re-matches #"^.{0,30}$" %)))
(s/defschema AddressNeighborhood (s/pred #(re-matches #"^.{1,80}$" %)))
(s/defschema AddressLocality (s/pred #(re-matches #"^.{0,80}$" %)))
(s/defschema AddressMunicipality (s/pred #(re-matches #"^.{1,100}$" %)))
(s/defschema AddressCountry (s/pred #(re-matches #"^.{1,50}$" %)))
(s/defschema AddressZipCode (s/pred #(re-matches #"^.{0,20}$" %)))
(def address-skeleton {:street          {:required true :schema AddressStreet}
                       :zip-code        {:required true :schema AddressZipCode}
                       :municipality    {:required true :schema AddressMunicipality}
                       :state           {:required true :schema s/Str}
                       :country         {:required true :schema AddressCountry}

                       :exterior-number {:required false :schema AddressExteriorNumber}
                       :interior-number {:required false :schmea AddressInteriorNumber}
                       :neighbordhood   {:required false :schema AddressNeighborhood}
                       :locality        {:required false :schema AddressLocality}})
(s/defschema Address (csc/loose-schema address-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ReceiverViewModel
(def receiver-view-model-skeleton {:rfc     {:required true :schema RFC}
                                   :name    {:required true :schema s/Str}

                                   :address {:required false :schema Address}
                                   :email   {:required false :schema s/Str}})
(s/defschema ReceiverViewModel (csc/loose-schema receiver-view-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=CsdBindingModel
(def csd-binding-model-skeleton {:certificate          {:required true :schema s/Str}
                                 :private-key          {:required true :schema s/Str}
                                 :private-key-password {:required true :schema s/Str}})
(s/defschema CsdBindingModel (csc/loose-schema csd-binding-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=TaxEntityInfoViewModel
(def tax-entity-info-view-model-skeleton {:fiscal-regime  {:required true :schema s/Str}
                                          :rfc            {:required true :schema RFC}
                                          :tax-name       {:required true :schema s/Str}
                                          :phone          {:required true :schema s/Str}

                                          :comercial-name {:required false :schema s/Str}
                                          :email          {:required false :schema s/Str}
                                          :optional-email {:required false :schema s/Str}
                                          :tax-address    {:required false :schema Address}
                                          :issued-in      {:required false :schema Address}
                                          :csd            {:required false :schema CsdBindingModel}
                                          :password-sat   {:required false :schema s/Str}
                                          :uri-logo       {:required false :schema s/Str}})
(s/defschema TaxEntityInfoViewModel (csc/loose-schema tax-entity-info-view-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=EducationalInstitution
(s/defschema EducationLevel (s/pred #(re-matches #"^Preescolar|Primaria|Secundaria|Profesional técnico|Bachillerato o su equivalente$" %)))
(def educational-institution-skeleton {:students-name   {:required true :schema s/Str}
                                       :curp            {:required true :schema CURP}
                                       :education-level {:required true :schema EducationLevel}
                                       :aut-rvoe        {:required true :schema s/Str}

                                       :payment-rfc     {:required false :schema RFC}})
(s/defschema EducationalInstitution (csc/loose-schema educational-institution-skeleton))


;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ThirdPartyAddress


(s/defschema ThirdPartyAddressStreet (s/pred #(re-matches #"^.+$" %)))
(s/defschema ThirdPartyAddressExteriorNumber (s/pred #(re-matches #"^.+$" %)))
(s/defschema ThirdPartyAddressInteriorNumber (s/pred #(re-matches #"^.+$" %)))
(s/defschema ThirdPartyAddressNeighborhood (s/pred #(re-matches #"^.+$" %)))
(s/defschema ThirdPartyAddressLocality (s/pred #(re-matches #"^.+$" %)))
(s/defschema ThirdPartyAddressMunicipality (s/pred #(re-matches #"^.+$" %)))
(s/defschema ThirdPartyAddressCountry (s/pred #(re-matches #"^.+$" %)))
(s/defschema ThirdPartyAddressReference (s/pred #(re-matches #"^.+$" %)))
(def third-party-address-skeleton {:street          {:required true :schema ThirdPartyAddressStreet}
                                   :municipality    {:required true :schema ThirdPartyAddressMunicipality}
                                   :state           {:required true :schema s/Str}
                                   :country         {:required true :schema ThirdPartyAddressCountry}
                                   :postal-code     {:required true :schema MexicanPostalCode}

                                   :exterior-number {:required false :schema ThirdPartyAddressExteriorNumber}
                                   :interior-number {:required false :schmea ThirdPartyAddressInteriorNumber}
                                   :neighbordhood   {:required false :schema ThirdPartyAddressNeighborhood}
                                   :locality        {:required false :schema ThirdPartyAddressLocality}
                                   :reference       {:required false :schema ThirdPartyAddressReference}})
(s/defschema ThirdPartyAddress (csc/loose-schema third-party-address-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=CustomsInformation
(s/defschema CustomsNumber (s/pred #(re-matches #"^.+$" %)))
(s/defschema CustomsName (s/pred #(re-matches #"^.+$" %)))
(def customs-information-skeleton {:number  {:required true :schema CustomsNumber}
                                   :date    {:required true :schema LocalDateTime}

                                   :customs {:required false :schema CustomsName}})
(s/defschema CustomsInformation (csc/loose-schema customs-information-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Part
(def part-skeleton {:quantity              {:required true :schema s/Num}
                    :description           {:required true :schema s/Str}

                    :unit                  {:required false :schema s/Str}
                    :identification-number {:required false :schema s/Str}
                    :unit-price            {:required false :schema s/Num}
                    :amount                {:required false :schema s/Num}
                    :customs-information   {:required false :schema [CustomsInformation]}})
(s/defschema Part (csc/loose-schema part-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ThirdPartyAccountTax
(s/defschema ThirdPartyAccountTaxTaxName (s/pred #(re-matches #"^IVA ?RET|ISR|IEPS|IVA$" %)))
(def third-party-account-tax-skeleton {:name   {:required true :schema ThirdPartyAccountTaxTaxName}
                                       :amount {:required true :schema s/Num}

                                       :rate   {:required false :schema s/Num}})
(s/defschema ThirdPartyAccountTax (csc/loose-schema third-party-account-tax-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ThirdPartyAccount
(def third-party-account-skeleton {:rfc                   {:required true :schema RFC}
                                   :taxes                 {:required true :schema [ThirdPartyAccountTax]}

                                   :name                  {:required false :schema s/Str}
                                   :third-tax-information {:required false :schema ThirdPartyAddress}
                                   :customs-information   {:required false :schema CustomsInformation}
                                   :parts                 {:required false :schema [Part]}
                                   :property-tax-number   {:required false :schema s/Str}})
(s/defschema ThirdPartyAccount (csc/loose-schema third-party-account-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ItemComplement
(def item-complement-skeleton {:educational-institution {:required false :schema EducationalInstitution}
                               :third-party-account     {:required false :schema ThirdPartyAccount}})
(s/defschema ItemComplement (csc/loose-schema item-complement-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ItemInfoModel
(def item-info-model-skeleton {:quantity    {:required true :schema s/Num}
                               :unit        {:required true :schema s/Str}
                               :description {:required true :schema s/Str}
                               :unit-value  {:required true :schema s/Num}
                               :total       {:required true :schema s/Num}

                               :complement  {:required false :schema ItemComplement}})
(s/defschema ItemInfoModel (csc/loose-schema item-info-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=TaxInfoModel
(def tax-info-model-skeleton {:total {:required true :schema s/Num}
                              :name  {:required true :schema s/Str}
                              :rate  {:required true :schema s/Num}
                              :type  {:required true :schema s/Str}})
(s/defschema TaxInfoModel (csc/loose-schema tax-info-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=CfdiRelation
(def cfdi-relation-skeleton {:uuid  {:required true :schema s/Str}})
(s/defschema CfdiRelation (csc/loose-schema cfdi-relation-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=CfdiRelations
(def cfdi-relations-skeleton {:type  {:required true :schema s/Str}
                              :cfdis {:required true :schema [CfdiRelation]}})
(s/defschema CfdiRelations (csc/loose-schema cfdi-relations-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ItemFullBindingModel
(s/defschema ItemFullBindingModelIdentificationNumber (s/pred #(re-matches #"^[^|]{0,50}$" %)))
(s/defschema ItemFullBindingModelUnit (s/pred #(re-matches #"^.{1,20}$" %)))
(s/defschema ItemFullBindingModelDescription (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(def item-full-binding-model-skeleton {:product-code          {:required true :schema s-cft/ProductCode}
                                       :description           {:required true :schema ItemFullBindingModelDescription}
                                       :unit-code             {:required true :schema s-cft/UnitCode}
                                       :unit-price            {:required true :schema s/Num}
                                       :quantity              {:required true :schema s/Num}
                                       :subtotal              {:required true :schema s/Num}
                                       :taxes                 {:required true :schema [TaxBindingModel]}
                                       :total                 {:required true :schema s/Num}

                                       :identification-number {:required false :schema ItemFullBindingModelIdentificationNumber}
                                       :unit                  {:required false :schema ItemFullBindingModelUnit}
                                       :discount              {:required false :schema s/Num}
                                       :cuenta-predial        {:required false :schema s/Str}
                                       :numeros-pedimento     {:required false :schema [s/Str]}
                                       :complement            {:required false :schema ItemComplement}})
(s/defschema ItemFullBindingModel (csc/loose-schema item-full-binding-model-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Donat
(def donation-skeleton {:authorization-number {:required true :schema s/Str}
                        :authorization-date   {:required true :schema LocalDateTime}
                        :legend               {:required true :schema s/Str}})
(s/defschema Donation (csc/loose-schema donation-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=AddressEmisor
(s/defschema EmisorStreet (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(s/defschema EmisorExteriorNumber (s/pred #(re-matches #"^[^|]{1,55}$" %)))
(s/defschema EmisorInteriorNumber (s/pred #(re-matches #"^[^|]{1,55}$" %)))
(def address-emisor-skeleton {:street          {:required true :schema EmisorStreet}

                              :exterior-number {:required false :schema EmisorExteriorNumber}
                              :interior-number {:required false :schema EmisorInteriorNumber}
                              :neighborhood    {:required false :schema s/Str}
                              :reference       {:required false :schema s/Str}
                              :zip-code        {:required false :schema MexicanPostalCode}})
(s/defschema AddressEmisor (csc/loose-schema address-emisor-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Issuer
(def issuer-skeleton {:address {:required true :schema AddressEmisor}
                      :curp    {:required true :schema CURP}})
(s/defschema Issuer (csc/loose-schema issuer-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=CEAddress
(s/defschema CEAddressStreet (s/pred #(re-matches #"^[^|]{1,55}$" %)))
(s/defschema CEAddressExteriorNumber (s/pred #(re-matches #"^[^|]{1,55}$" %)))
(s/defschema CEAddressInteriorNumber (s/pred #(re-matches #"^[^|]{1,55}$" %)))
(s/defschema CEAddressNeighborhood (s/pred #(re-matches #"^[^|]{1,120}$" %)))
(s/defschema CEAddressReference (s/pred #(re-matches #"^[^|]{1,250}$" %)))
(s/defschema CEAddressMunicipality (s/pred #(re-matches #"^[^|]{1,120}$" %)))
(s/defschema CEAddressState (s/pred #(re-matches #"^[^|]{1,30}$" %)))
(s/defschema CEAddressZipcode (s/pred #(re-matches #"^.{1,12}$" %)))
(def ce-address-skeleton {:street          {:required true :schema CEAddressStreet}
                          :country         {:required true :schema s/Str}
                          :zip-code        {:required true :schema CEAddressZipcode}

                          :exterior-number {:required false :schema CEAddressExteriorNumber}
                          :interior-number {:required false :schema CEAddressInteriorNumber}
                          :neighborhood    {:required false :schema CEAddressNeighborhood}
                          :reference       {:required false :schema CEAddressReference}
                          :locality        {:required false :schema s/Str}
                          :municipality    {:required false :schema CEAddressMunicipality}
                          :state           {:required false :schema CEAddressState}})
(s/defschema CEAddress (csc/loose-schema ce-address-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Receiver
(def receiver-skeleton {:address {:required true :schema CEAddress}})
(s/defschema Receiver (csc/loose-schema receiver-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Owner
(def owner-skeleton {:num-reg-id-trib {:required true :schema s/Str}
                     :tax-residence   {:required true :schema s/Str}})
(s/defschema Owner (csc/loose-schema owner-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Recipient
(s/defschema RecipientName (s/pred #(re-matches #"^[^|]{1,300}$" %)))
(s/defschema RecipientNumRegIdTrib (s/pred #(re-matches #"^.{4,40}$" %)))
(def recipient-skeleton {:name            {:required false :schema RecipientName}
                         :num-reg-id-trib {:required false :schema RecipientNumRegIdTrib}
                         :addresses       {:required false :schema [CEAddress]}})
(s/defschema Recipient (csc/loose-schema recipient-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=SpecificDescriptions
(s/defschema SpecificDescriptionsBrand (s/pred #(re-matches #"^[^|]{1,35}$" %)))
(s/defschema SpecificDescriptionsModel (s/pred #(re-matches #"^[^|]{1,80}$" %)))
(s/defschema SpecificDescriptionsSubModel (s/pred #(re-matches #"^[^|]{1,50}$" %)))
(s/defschema SpecificDescriptionsSerialNumber (s/pred #(re-matches #"^[^|]{1,40}$" %)))
(def specific-descriptions-skeleton {:brand         {:required true :schema SpecificDescriptionsBrand}

                                     :model         {:required false :schema SpecificDescriptionsModel}
                                     :sub-model     {:required false :schema SpecificDescriptionsSubModel}
                                     :serial-number {:required false :schema SpecificDescriptionsSerialNumber}})
(s/defschema SpecificDescriptions (csc/loose-schema specific-descriptions-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Commodity
(s/defschema CommodityIdentificationNumber (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(def commodity-skeleton {:identification-number {:required true :schema CommodityIdentificationNumber}
                         :value-in-dolar        {:required true :schema s/Num}

                         :specific-descriptions {:required false :schema [SpecificDescriptions]}
                         :tariff-fraction       {:required false :schema s/Str}
                         :customs-quantity      {:required false :schema s/Num}
                         :customs-unit          {:required false :schema s/Str}
                         :customs-unit-value    {:required false :schema s/Num}})
(s/defschema Commodity (csc/loose-schema commodity-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ForeignTrade
(s/defschema ForeignTradeOriginCertificateNumber (s/pred #(re-matches #"^[^|]{6,40}$" %)))
(s/defschema ForeignTradeReliableExporterNumber (s/pred #(re-matches #"^[^|]{1,50}$" %)))
(s/defschema ForeignTradeObservations (s/pred #(re-matches #"^[^|]{1,300}$" %)))
(def foreign-trade-skeleton {:receiver                  {:required true :schema Receiver}
                             :operation-type            {:required true :schema s/Str}

                             :issuer                    {:required false :schema Issuer}
                             :owner                     {:required false :schema [Owner]}
                             :recipient                 {:required false :schema [Recipient]}
                             :reason-for-transfer       {:required false :schema s/Str}
                             :commodity                 {:required false :schema [Commodity]}
                             :request-code              {:required false :schema s/Str}
                             :incoterm                  {:required false :schema s/Str}
                             :subdivision               {:required false :schema s/Bool}
                             :exchange-rate-u-s-d       {:required false :schema s/Num}
                             :total-u-s-d               {:required false :schema s/Num}
                             :origin-certificate        {:required false :schema s/Bool}
                             :origin-certificate-number {:required false :schema ForeignTradeOriginCertificateNumber}
                             :reliable-exporter-number  {:required false :schema ForeignTradeReliableExporterNumber}
                             :observations              {:required false :schema ForeignTradeObservations}})
(s/defschema ForeignTrade (csc/loose-schema foreign-trade-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Outsourcing
(def outsourcing-skeleton {:rfc-contractor  {:required false :schema s/Str}
                           :percentage-time {:required false :schema s/Num}})
(s/defschema Outsourcing (csc/loose-schema outsourcing-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Employee
(s/defschema EmployeeNumber (s/pred #(re-matches #"^[^|]{1,15}$" %)))
(s/defschema EmployeeSocialSecurityNumber (s/pred #(re-matches #"^[0-9]{1,15}$" %)))
(s/defschema EmployeeDepartment (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(s/defschema EmployeePosition (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(s/defschema EmployeeBankAccount (s/pred #(re-matches #"^[0-9]{10,18}$" %)))
(def employee-skeleton {:curp                       {:required true :schema CURP}
                        :contract-type              {:required true :schema s/Str}
                        :regime-type                {:required true :schema s/Str}
                        :employee-number            {:required true :schema EmployeeNumber}
                        :federal-entity-key         {:required true :schema s/Str}

                        :outsourcing                {:required false :schema [Outsourcing]}
                        :social-security-number     {:required false :schema EmployeeSocialSecurityNumber}
                        :start-date-labor-relations {:required false :schema LocalDate}
                        :unionized                  {:required false :schema s/Bool}
                        :type-of-journey            {:required false :schema s/Str}
                        :department                 {:required false :schema EmployeeDepartment}
                        :position                   {:required false :schema EmployeePosition}
                        :position-risk              {:required false :schema s/Str}
                        :bank                       {:required false :schema s/Str}
                        :bank-account               {:required false :schema EmployeeBankAccount}
                        :base-salary                {:required false :schema s/Num}
                        :daily-salary               {:required false :schema s/Num}})
(s/defschema Employee (csc/loose-schema employee-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ActionsOrTitles
(def actions-or-titles-skeleton {:market-value        {:required true :schema s/Num}
                                 :price-when-granting {:required true :schema s/Num}})
(s/defschema ActionsOrTitles (csc/loose-schema actions-or-titles-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=ExtraHour
(def extra-hour-skeleton {:days        {:required true :schema s/Int}
                          :hours-type  {:required true :schema s/Str}
                          :extra-hours {:required true :schema s/Int}
                          :paid-amount {:required true :schema s/Num}})
(s/defschema ExtraHour (csc/loose-schema extra-hour-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Perception
(s/defschema PerceptionCode (s/pred #(re-matches #"^[^|]{3,15}$" %)))
(s/defschema PerceptionDescription (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(def perception-skeleton {:taxed-amount      {:required true :schema s/Int}
                          :exempt-amount     {:required true :schema s/Int}

                          :actions-or-titles {:required false :schema ActionsOrTitles}
                          :extra-hours       {:required false :schema [ExtraHour]}
                          :code              {:required false :schema PerceptionCode}
                          :description       {:required false :schema PerceptionDescription}})
(s/defschema Perception (csc/loose-schema perception-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Retirement
(def retirement-skeleton {:total-a-single-payment {:required false :schema s/Num}
                          :total-parciality       {:required false :schema s/Num}
                          :daily-amount           {:required false :schema s/Num}
                          :accumulated-income     {:required false :schema s/Num}
                          :non-accumulated-income {:required false :schema s/Num}})
(s/defschema Retirement (csc/loose-schema retirement-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Indemnification
(def indemnification-skeleton {:accumulated-income      {:required true :schema s/Int}
                               :non-accumulated-income  {:required true :schema s/Int}

                               :total-paid              {:required false :schema s/Num}
                               :years-of-service        {:required false :schema s/Int}
                               :last-monthly-salary-ord {:required false :schema s/Int}})

(s/defschema Indemnification (csc/loose-schema indemnification-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Perceptions
(def perceptions-skeleton {:details         {:required false :schema [Perception]}
                           :retirement      {:required false :schema Retirement}
                           :indemnification {:required false :schema Indemnification}})
(s/defschema Perceptions (csc/loose-schema perceptions-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Deduction
(s/defschema DeductionCode (s/pred #(re-matches #"^[^|]{3,15}$" %)))
(s/defschema DeductionDescription (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(def deduction-skeleton {:deduccion-type {:required true :schema s/Str}
                         :code           {:required true :schema DeductionCode}
                         :description    {:required true :schema DeductionDescription}
                         :amount         {:required true :schema s/Num}})
(s/defschema Deduction (csc/loose-schema deduction-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Deductions
(def deductions-skeleton {:details {:required true :schema [Deduction]}})
(s/defschema Deductions (csc/loose-schema deductions-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=EmploymentSubsidy
(def employment-subsidy-skeleton {:amount {:required true :schema s/Int}})
(s/defschema EmploymentSubsidy (csc/loose-schema employment-subsidy-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Compensation
(def compensation-skeleton {:positive-balance           {:required true :schema s/Num}
                            :year                       {:required true :schema s/Int}
                            :remaining-positive-balance {:required true :schema s/Num}})
(s/defschema Compensation (csc/loose-schema compensation-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=OtherPayment)
(s/defschema OtherPaymentCode (s/pred #(re-matches #"^[^|]{3,15}$" %)))
(s/defschema OtherPaymentDescription (s/pred #(re-matches #"^[^|]{1,100}$" %)))
(def other-payment-skeleton {:other-payment-type {:required true :schema s/Str}
                             :code               {:required true :schema OtherPaymentCode}
                             :description        {:required true :schema OtherPaymentDescription}
                             :amount             {:required true :schema s/Num}

                             :employment-subsidy {:required false :schema EmploymentSubsidy}
                             :compensation       {:required false :schema Compensation}})
(s/defschema OtherPayment (csc/loose-schema other-payment-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Incapacity
(def incapacity-skeleton {:days   {:required true :schema s/Int}
                          :type   {:required true :schema s/Str}

                          :amount {:required false :schema s/Num}})
(s/defschema Incapacity (csc/loose-schema incapacity-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=EntitySNCF
(s/defschema OriginSource (s/pred #(re-matches #"^IP|IM|IF$" %)))
(def entity-sncf-skeleton {:origin-source        {:required true :schema OriginSource}
                           :amount-origin-source {:required true :schema s/Num}})
(s/defschema EntitySNCF (csc/loose-schema entity-sncf-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=PayrollIssuer
(s/defschema EmployerRegistration (s/pred #(re-matches #"^[^| ]{1,20}$" %)))
(def payroll-issuer-skeleton {:entity-s-n-c-f        {:required false :schema EntitySNCF}
                              :curp                  {:required false :schema CURP}
                              :employer-registration {:required false :schema EmployerRegistration}
                              :from-employer-rfc     {:required false :schema RFC}})
(s/defschema PayrollIssuer (csc/loose-schema payroll-issuer-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Payroll
(def payroll-skeleton {:employee             {:required true :schema Employee}
                       :type                 {:required true :schema s/Str}
                       :payment-date         {:required true :schema LocalDateTime}
                       :initial-payment-date {:required true :schema LocalDateTime}
                       :final-payment-date   {:required true :schema LocalDateTime}
                       :days-paid            {:required true :schema s/Int}

                       :issuer               {:required false :schema PayrollIssuer}
                       :perceptions          {:required false :schema Perceptions}
                       :deductions           {:required false :schema Deductions}
                       :other-payments       {:required false :schema [OtherPayment]}
                       :incapacities         {:required false :schema [Incapacity]}})
(s/defschema Payroll (csc/loose-schema payroll-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=Complement
(def complement-skeleton {:payments      {:required true :schema [PaymentBindingModel]}
                          :donation      {:required true :schema Donation}
                          :foreign-trade {:required true :schema ForeignTrade}
                          :payroll       {:required true :schema Payroll}})
(s/defschema Complement (csc/loose-schema complement-skeleton))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; TOP LEVEL SCHEMAS ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;;;;;;;;;;;;;;;;;; Create CFDI Request and Response schemas for multiemisor API ;;;;;;;;;;;;;;;;;;;;;

;; Docs at: https://apisandbox.facturama.mx/docs-multi/api/POST-api-lite-2-cfdis


(s/defschema Folio (s/pred #(re-matches #"^[0-9]{1,9}?$" %)))
(s/defschema Serie (s/pred #(re-matches #"^[a-zA-z0-9]{1,10}$" %)))
(s/defschema PaymentAccountNumber (s/pred #(re-matches #"^\d{1,4}?$" %)))
(s/defschema CurrencySymbol (s/pred #(re-matches #"^.{3}$" %)))
(def create-cfdi-request-skeleton {:expedition-place       {:required true :schema MexicanPostalCode}
                                   :folio                  {:required true :schema Folio}
                                   :cfdi-type              {:required true :schema s-cft/CfdiTypeCode}
                                   :payment-method         {:required true :schema s-cft/PaymentMethodCode}
                                   :issuer                 {:required true :schema IssuerBindingModel}
                                   :receiver               {:required true :schema ReceiverBindingModel}
                                   :items                  {:required true :schema [ItemFullBindingModel]}

                                   :date                   {:required false :schema LocalDateTime} ;; client generated emission timestamp
                                   :serie                  {:required false :schema Serie}
                                   :payment-form           {:required false :schema s-cft/PaymentFormCode}

                                   :payment-account-number {:required false :schema PaymentAccountNumber}
                                   :currency-exchange-rate {:required false :schema s/Num}
                                   :currency               {:required false :schema CurrencySymbol}
                                   :payment-conditions     {:required false :schema s/Str}
                                   :relations              {:required false :schema [CfdiRelations]}
                                   :complemento            {:required false :schema Complement}})
(s/defschema CreateCFDIRequest (csc/loose-schema create-cfdi-request-skeleton))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=CfdiInfoModel
(def cfdi-info-model-skeleton {:id                     {:required true :schema s/Str}
                               :folio                  {:required true :schema s/Str}
                               :date                   {:required true :schema LocalDateTime} ;; client generated emission timestamp
                               :cert-number            {:required true :schema s/Str}
                               :subtotal               {:required true :schema s/Num}
                               :total                  {:required true :schema s/Num}
                               :complement             {:required true :schema ComplementModel}
                               :status                 {:required true :schema s/Str}

                               :cfdi-type              {:required true :schema s/Str}
                               :type                   {:required true :schema s/Str}
                               :serie                  {:required true :schema s/Str}
                               :payment-method         {:required true :schema s/Str}
                               :expedition-place       {:required true :schema MexicanPostalCode}
                               :exchange-rate          {:required true :schema s/Num}
                               :currency               {:required true :schema s/Str}
                               :discount               {:required true :schema s/Num}
                               :observations           {:required true :schema s/Str}
                               :issuer                 {:required true :schema TaxEntityInfoViewModel}
                               :receiver               {:required true :schema ReceiverViewModel}
                               :items                  {:required true :schema [ItemInfoModel]}
                               :taxes                  {:required true :schema [TaxInfoModel]}

                               :payment-terms          {:required false :schema s/Str}
                               :payment-conditions     {:required false :schema s/Str}
                               :payment-account-number {:required false :schema s/Str}
                               :order-number           {:required false :schema s/Str}})
(s/defschema CfdiInfoModel (csc/loose-schema cfdi-info-model-skeleton))

;;;;;;;;;;;;;;;;;;; Find CFDI by Key Request and Response schemas for multiemisor API ;;;;;;;;;;;;;;;;;;;;;
;; Docs at: https://apisandbox.facturama.mx/docs/api/GET-Cfdi_type_keyword_status_invoiceType

;; CFDI can be uniquely identified by: (receiver-rfc, folio)
(def cfdi-key {:receiver-rfc {:required true :schema RFC}
               :folio        {:required true :schema Folio}
               :serie        {:required true :schema Serie}
               :total        {:required true :schema s/Num}})
(s/defschema CFDIKey (csc/loose-schema cfdi-key))

;; https://apisandbox.facturama.mx/docs/ResourceModel?modelName=CfdiSearchViewModel
(def cfdi-search-view-model-skeleton {:id             {:required true :schema s/Str}
                                      :folio          {:required true :schema s/Str}
                                      :tax-name       {:required true :schema s/Str}
                                      :rfc            {:required true :schema RFC}
                                      :date           {:required true :schema LocalDateTime} ;; server generated stamping timestamp
                                      :total          {:required true :schema s/Num}
                                      :uuid           {:required true :schema s/Str}
                                      :is-active      {:required true :schema s/Bool}
                                      :status         {:required true :schema s/Str}

                                      :cfdi-type      {:required false :schema s/Str}
                                      :serie          {:required false :schema s/Str}
                                      :email          {:required false :schema s/Str}
                                      :email-sent     {:required false :schema s/Bool}
                                      :payment-method {:required false :schema s/Str}})
(s/defschema CfdiSearchViewModel (csc/loose-schema cfdi-search-view-model-skeleton))
