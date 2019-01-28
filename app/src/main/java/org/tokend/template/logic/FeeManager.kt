package org.tokend.template.logic

import io.reactivex.Single
import org.tokend.sdk.api.fees.params.FeeParams
import org.tokend.template.di.providers.ApiProvider
import org.tokend.rx.extensions.toSingle
import org.tokend.template.features.fees.model.FeeRecord
import org.tokend.wallet.xdr.FeeType
import org.tokend.wallet.xdr.PaymentFeeType
import java.math.BigDecimal

/**
 * Manages operation fees loading
 */
class FeeManager(
        private val apiProvider: ApiProvider
) {
    /**
     * @return fee for given operation params
     *
     * @param type [FeeType] value
     * @param subtype fee subtype, use 0 if there is no subtypes for required operation
     * @param accountId ID of the fee payer account
     * @param amount operation amount
     * @param asset operation asset
     */
    fun getFee(type: Int, subtype: Int,
               accountId: String, asset: String, amount: BigDecimal): Single<FeeRecord> {
        val signedApi = apiProvider.getSignedApi()
                ?: return Single.error(IllegalStateException("No signed API instance found"))

        return signedApi.fees.getByType(
                type,
                FeeParams(
                        asset,
                        accountId,
                        amount,
                        subtype)
        )
                .toSingle()
                .map { FeeRecord(it) }
    }

    /**
     * @return payment fee for given params
     *
     * @param isOutgoing controls subtype of payment fee
     *
     * @see PaymentFeeType
     * @see getFee
     */
    fun getPaymentFee(accountId: String, asset: String, amount: BigDecimal,
                      isOutgoing: Boolean): Single<FeeRecord> {
        val subtype =
                if (isOutgoing)
                    PaymentFeeType.OUTGOING.value
                else
                    PaymentFeeType.INCOMING.value
        return getFee(FeeType.PAYMENT_FEE.value, subtype, accountId, asset, amount)
    }

    /**
     * @return withdrawal fee for given params
     *
     * @see getFee
     */
    fun getWithdrawalFee(accountId: String, asset: String, amount: BigDecimal): Single<FeeRecord> {
        return getFee(FeeType.WITHDRAWAL_FEE.value, 0, accountId, asset, amount)
    }

    /**
     * @return offer fee for given params
     *
     * @see getFee
     */
    fun getOfferFee(accountId: String, asset: String, amount: BigDecimal): Single<FeeRecord> {
        return getFee(FeeType.OFFER_FEE.value, 0, accountId, asset, amount)
    }
}