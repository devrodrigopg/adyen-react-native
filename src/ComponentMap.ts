import { PaymentMethodsResponse } from './core/types';

/**
 * Find payment method in JSON response or \paymentMethods API
 */
export function find(paymentMethods: PaymentMethodsResponse, typeName: string) {
  return paymentMethods.paymentMethods.find(
    (pm) => pm.type === mapCreatedComponentType(typeName)
  );
}

/**
 * Map component name to txVariable name from \paymentMethod response
 * @param {string} pmType
 * @returns {string} matching txVariable name or original name
 */
function mapCreatedComponentType(pmType: string) {
  // Components created as 'card' need to be matched with paymentMethod response objects with type 'scheme'
  return pmType === 'card' ? 'scheme' : pmType;
}

export const UNSUPPORTED_PAYMENT_METHODS = [
  /** Payment methods that might be interpreted as redirect, but are actually not supported */
  'wechatpayMiniProgram',
  'wechatpayQR',
  'wechatpayWeb',
  'afterpay_default',
  'amazonpay',
  'qiwiwallet',
  'ratepay',
  'ratepay_directdebit',
  'bcmc_mobile_QR',

  /** Voucher payment methods that are not yet supported */
  'doku',
  'doku_alfamart',
  'doku_permata_lite_atm',
  'doku_indomaret',
  'doku_atm_mandiri_va',
  'doku_sinarmas_va',
  'doku_mandiri_va',
  'doku_cimb_va',
  'doku_danamon_va',
  'doku_bri_va',
  'doku_bni_va',
  'doku_bca_va',
  'doku_wallet',
  'oxxo',
  'multibanco',
  'econtext_atm',
  'econtext_online',
  'econtext_seven_eleven',
  'econtext_stores',
  'dragonpay_ebanking',
  'dragonpay_otc_banking',
  'dragonpay_otc_non_banking',
  'dragonpay_otc_philippines',

  /** Giftcard payment methods that are not yet supported */
  'giftcard',
  'mealVoucher_FR_natixis',
  'mealVoucher_FR_sodexo',
  'mealVoucher_FR_groupeup',

  /** Open Invoice payment methods that are not yet supported */
  'affirm',
  'atome',

  /** Wallet payment methods that are not yet supported */
  'cashapp',
  'clicktopay',
  'wechatpaySDK',
];

export const NATIVE_COMPONENTS = [
  /** Card */
  'card',
  'scheme',
  'bcmc',

  /** issuerList */
  'billdesk_online',
  'billdesk_wallet',
  'dotpay',
  'entercash',
  'eps',
  'ideal',
  'molpay_ebanking_fpx_MY',
  'molpay_ebanking_TH',
  'molpay_ebanking_VN',
  'onlineBanking',
  'onlineBanking_CZ',
  'onlinebanking_IN',
  'onlineBanking_PL',
  'onlineBanking_SK',
  'paybybank',
  'wallet_IN',

  /** Await */
  'blik',
  'mbway',
  'upi',
  'upi_qr',
  'upi_collect',

  /** Direct debit */
  'ach',
  'directdebit_GB',
  'sepadirectdebit',

  /** Voucher payment methods that are not yet supported */
  'boletobancario',
  'boletobancario_bancodobrasil',
  'boletobancario_bradesco',
  'boletobancario_hsbc',
  'boletobancario_itau',
  'boletobancario_santander',
  'primeiropay_boleto'
];
