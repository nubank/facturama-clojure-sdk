(ns facturama.multiemisor.models.cfdi-field-types
  (:require [common-core.types.loose-enum :as loose-enum]
            [schema.core :as s]))

;;;;;;;;;;;;;;;; Abbreviated Code Catalogue for CFDI field types ;;;;;;;;;;;;;;;;;;;
;;
;; See below links for official documents/guidelines:
;;
;; (Index) http://omawww.sat.gob.mx/informacion_fiscal/factura_electronica/Paginas/Anexo_20_version3.3.aspx
;; (Guidelines) http://omawww.sat.gob.mx/informacion_fiscal/factura_electronica/Documents/Gu%C3%ADaAnexo20.pdf
;; (Code Catalogue) http://omawww.sat.gob.mx/informacion_fiscal/factura_electronica/Documents/catCFDI.xls

;; CfdiType (referred to as "TipoDeComprobante" in official docs)
(def ^:private cfdi-type-map
  {:nomina   :N
   :pago     :P
   :ingreso  :I
   :egreso   :E
   :traslado :T})
(def CfdiTypeName (loose-enum/from-enum (apply s/enum (keys cfdi-type-map))))
(def CfdiTypeCode (loose-enum/from-enum (apply s/enum (vals cfdi-type-map))))
(s/defn cfdi-type-name->code :- CfdiTypeCode [name :- CfdiTypeName] (name cfdi-type-map))

;; CfdiUse (referred to as "UsoCFDI" in official docs)
(def ^:private cfdi-use-map
  {:adquisicion-de-mercancias                                                           :G01
   :devoluciones-descuentos-o-bonificaciones                                            :G02
   :gastos-en-general                                                                   :G03
   :construcciones                                                                      :I01
   :mobilario-y-equipo-de-oficina-por-inversiones                                       :I02
   :equipo-de-transporte                                                                :I03
   :equipo-de-computo-y-accesorios                                                      :I04
   :dados-troqueles-moldes-matrices-y-herramental                                       :I05
   :comunicaciones-telefonicas                                                          :I06
   :comunicaciones-satelitales                                                          :I07
   :otra-maquinaria-y-equipo                                                            :I08
   :honorarios-medicos-dentales-y-gastos-hospitalarios                                  :D01
   :gastos-medicos-por-incapacidad-o-discapacidad                                       :D02
   :gastos-funerales                                                                    :D03
   :donativos                                                                           :D04
   :intereses-reales-efectivamente-pagados-por-creditos-hipotecarios-casa-habitacion    :D05
   :aportaciones-voluntarias-al-sar                                                     :D06
   :primas-por-seguros-de-gastos-medicos                                                :D07
   :gastos-de-transportacion-escolar-obligatoria                                        :D08
   :depositos-en-cuentas-para-el-ahorro-primas-que-tengan-como-base-planes-de-pensiones :D09
   :pagos-por-servicios-educativos-colegiaturas                                         :D10
   :por-definir                                                                         :P01})
(def CfdiUseName (loose-enum/from-enum (apply s/enum (keys cfdi-use-map))))
(def CfdiUseCode (loose-enum/from-enum (apply s/enum (vals cfdi-use-map))))
(s/defn cfdi-use-name->code :- CfdiUseCode [name :- CfdiUseName] (name cfdi-use-map))

;; PaymentForm (referred to as "FormaPago" in official docs)
(def ^:private payment-form-map
  {:efectivo                            :01
   :cheque-nominativo                   :02
   :transferencia-electronica-de-fondos :03
   :tarjeta-de-credito                  :04
   :monedero-electronico                :05
   :dinero-electronico                  :06
   :vales-de-despensa                   :08
   :dacion-en-pago                      :12
   :pago-por-subrogacion                :13
   :pago-por-consignacion               :14
   :condonacion                         :15
   :compensacion                        :17
   :novacion                            :23
   :confusion                           :24
   :remision-de-deuda                   :25
   :prescripcion-o-caducidad            :26
   :a-satisfaccion-del-acreedor         :27
   :tarjeta-de-debito                   :28
   :tarjeta-de-servicios                :29
   :aplicacion-de-anticipos             :30
   :intermediario-pagos                 :31
   :por-definir                         :99})
(def PaymentFormName (loose-enum/from-enum (apply s/enum (keys payment-form-map))))
(def PaymentFormCode (loose-enum/from-enum (apply s/enum (vals payment-form-map))))
(s/defn payment-form-name->code :- PaymentFormCode [name :- PaymentFormName] (name payment-form-map))

;; PaymentMethod (referred to as "MetodoPago" in official docs)
(def ^:private payment-method-map
  {:pago-en-una-sola-exhibicion      :PUE
   :pago-en-parcialidades-o-diferido :PPD})
(def PaymentMethodName (loose-enum/from-enum (apply s/enum (keys payment-method-map))))
(def PaymentMethodCode (loose-enum/from-enum (apply s/enum (vals payment-method-map))))
(s/defn payment-method-name->code :- PaymentMethodCode [name :- PaymentMethodName] (name payment-method-map))

;; ProductCode (referred to as "ClaveProdServ" in official docs)
(def ^:private product-code-map
  {:instituciones-bancarias :84121500})
(def ProductName (loose-enum/from-enum (apply s/enum (keys product-code-map))))
(def ProductCode (loose-enum/from-enum (apply s/enum (vals product-code-map))))
(s/defn product-name->code :- ProductCode [name :- ProductName] (name product-code-map))

;; UnitCode (referred to as "ClaveUnidad" in official docs)
(def ^:private unit-code-map
  {:unidad-de-servicio :E48})
(def UnitName (loose-enum/from-enum (apply s/enum (keys unit-code-map))))
(def UnitCode (loose-enum/from-enum (apply s/enum (vals unit-code-map))))
(s/defn unit-name->code :- UnitCode [name :- UnitName] (name unit-code-map))

;; FiscalRegime (referred to as "RegimenFiscal" in official docs)
(def ^:private fiscal-regime-map
  {:general-de-ley-personas-morales                                          :601
   :personas-morales-con-fines-no-lucrativos                                 :603
   :sueldos-y-salarios-e-ingresos-asimilados-a-salarios                      :605
   :arrendamiento                                                            :606
   :demas-ingresos                                                           :608
   :consolidacion                                                            :609
   :residentes-en-el-extranjero-sin-establecimiento-permanente-en-mexico     :610
   :ingresos-por-dividendos-socios-y-accionistas                             :611
   :personas-fisicas-con-actividades-empresariales-y-profesionales           :612
   :ingresos-por-intereses                                                   :614
   :sin-obligaciones-fiscales                                                :616
   :sociedades-cooperativas-de-produccion-que-optan-por-diferir-sus-ingresos :620
   :incorporacion-fiscal                                                     :621
   :actividades-agricolas-ganaderas-silvicolas-y-pesqueras                   :622
   :opcional-para-grupos-de-sociedades                                       :623
   :coordinados                                                              :624
   :hidrocarburos                                                            :628
   :regimen-de-enajenacion-o-adquisicion-de-bienes                           :607
   :de-los-regimenes-fiscales-preferentes-y-de-las-empresas-multinacionales  :629
   :enajenacion-de-acciones-en-bolsa-de-valores                              :630
   :regimen-de-los-ingresos-por-obtencion-de-premios                         :615})
(def FiscalRegimeName (loose-enum/from-enum (apply s/enum (keys fiscal-regime-map))))
(def FiscalRegimeCode (loose-enum/from-enum (apply s/enum (vals fiscal-regime-map))))
(s/defn fiscal-regime-name->code :- FiscalRegimeCode [name :- FiscalRegimeName] (name fiscal-regime-map))

(def TaxName ;; (referred to as "Impuesto / Tipo de Impuesto" in official docs)
  (loose-enum/from-enum
   (s/enum :IVA
           :ISR
           :IEPS)))

(def ThirdPartyName
  (loose-enum/from-enum
   (s/enum :facturama)))

(def CfdiVersion
  (loose-enum/from-enum
   (s/enum :3.3)))
