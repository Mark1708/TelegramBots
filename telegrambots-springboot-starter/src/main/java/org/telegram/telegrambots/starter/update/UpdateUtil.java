package org.telegram.telegrambots.starter.update;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.business.BusinessConnection;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.payments.PaidMediaPurchased;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.reactions.MessageReactionUpdated;
import org.telegram.telegrambots.starter.handler.UpdateType;

public class UpdateUtil {

    public static UpdateHolder<?> processUpdate(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            return new UpdateHolder<>(update.getUpdateId(), update, message, UpdateType.MESSAGE, message.getFrom(), message.getText());
        } else if (update.hasInlineQuery()) {
            InlineQuery inlineQuery = update.getInlineQuery();
            return new UpdateHolder<>(update.getUpdateId(), update, inlineQuery, UpdateType.INLINE_QUERY, inlineQuery.getFrom(), inlineQuery.getQuery());
        } else if (update.hasChosenInlineQuery()) {
            ChosenInlineQuery chosenInlineQuery = update.getChosenInlineQuery();
            return new UpdateHolder<>(update.getUpdateId(), update, chosenInlineQuery, UpdateType.CHOSEN_INLINE_QUERY_RESULT, chosenInlineQuery.getFrom(), chosenInlineQuery.getQuery());
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return new UpdateHolder<>(update.getUpdateId(), update, callbackQuery, UpdateType.CALLBACK_QUERY, callbackQuery.getFrom(), callbackQuery.getData());
        } else if (update.hasEditedMessage()) {
            Message editedMessage = update.getEditedMessage();
            return new UpdateHolder<>(update.getUpdateId(), update, editedMessage, UpdateType.EDITED_MESSAGE, editedMessage.getFrom(), editedMessage.getText());
        } else if (update.hasChannelPost()) {
            Message channelPost = update.getChannelPost();
            return new UpdateHolder<>(update.getUpdateId(), update, channelPost, UpdateType.CHANNEL_POST, channelPost.getFrom(), channelPost.getText());
        } else if (update.hasEditedChannelPost()) {
            Message editedChannelPost = update.getEditedChannelPost();
            return new UpdateHolder<>(update.getUpdateId(), update, editedChannelPost, UpdateType.EDITED_CHANNEL_POST, editedChannelPost.getFrom(), editedChannelPost.getText());
        } else if (update.hasShippingQuery()) {
            ShippingQuery shippingQuery = update.getShippingQuery();
            return new UpdateHolder<>(update.getUpdateId(), update, shippingQuery, UpdateType.SHIPPING_QUERY, shippingQuery.getFrom(), shippingQuery.getInvoicePayload());
        } else if (update.hasPreCheckoutQuery()) {
            PreCheckoutQuery preCheckoutQuery = update.getPreCheckoutQuery();
            return new UpdateHolder<>(update.getUpdateId(), update, preCheckoutQuery, UpdateType.PRE_CHECKOUT_QUERY, preCheckoutQuery.getFrom(), preCheckoutQuery.getInvoicePayload());
        } else if (update.hasPoll()) {
            Poll poll = update.getPoll();
            return new UpdateHolder<>(update.getUpdateId(), update, poll, UpdateType.POLL, null, poll.getQuestion());
        } else if (update.hasPollAnswer()) {
            PollAnswer pollAnswer = update.getPollAnswer();
            return new UpdateHolder<>(update.getUpdateId(), update, pollAnswer, UpdateType.POLL_ANSWER, pollAnswer.getUser(), null);
        } else if (update.hasMyChatMember()) {
            ChatMemberUpdated myChatMember = update.getMyChatMember();
            return new UpdateHolder<>(update.getUpdateId(), update, myChatMember, UpdateType.MY_CHAT_MEMBER, myChatMember.getFrom(), null);
        } else if (update.hasChatMember()) {
            ChatMemberUpdated chatMember = update.getChatMember();
            return new UpdateHolder<>(update.getUpdateId(), update, chatMember, UpdateType.CHAT_MEMBER, chatMember.getFrom(), null);
        } else if (update.hasChatJoinRequest()) {
            ChatJoinRequest chatJoinRequest = update.getChatJoinRequest();
            return new UpdateHolder<>(update.getUpdateId(), update, chatJoinRequest, UpdateType.CHAT_JOIN_REQUEST, chatJoinRequest.getUser(), null);
        } else if (update.hasMessageReaction()) {
            MessageReactionUpdated messageReaction = update.getMessageReaction();
            return new UpdateHolder<>(update.getUpdateId(), update, messageReaction, UpdateType.MESSAGE_REACTION, messageReaction.getUser(), null);
        } else if (update.hasMessageReactionCount()) {
            return new UpdateHolder<>(update.getUpdateId(), update, update.getMessageReactionCount(), UpdateType.MESSAGE_REACTION_COUNT, null, null);
        } else if (update.hasChatBoost()) {
            return new UpdateHolder<>(update.getUpdateId(), update, update.getChatBoost(), UpdateType.CHAT_BOOST, null, null);
        } else if (update.hasRemovedChatBoost()) {
            return new UpdateHolder<>(update.getUpdateId(), update, update.getRemovedChatBoost(), UpdateType.REMOVED_CHAT_BOOST, null, null);
        } else if (update.hasBusinessConnection()) {
            BusinessConnection businessConnection = update.getBusinessConnection();
            return new UpdateHolder<>(update.getUpdateId(), update, businessConnection, UpdateType.BUSINESS_CONNECTION, businessConnection.getUser(), null);
        } else if (update.hasBusinessMessage()) {
            Message businessMessage = update.getBusinessMessage();
            return new UpdateHolder<>(update.getUpdateId(), update, businessMessage, UpdateType.BUSINESS_MESSAGE, businessMessage.getFrom(), businessMessage.getText());
        } else if (update.hasEditedBusinessMessage()) {
            Message editedBuinessMessage = update.getEditedBuinessMessage();
            return new UpdateHolder<>(update.getUpdateId(), update, editedBuinessMessage, UpdateType.EDITED_BUSINESS_MESSAGE, editedBuinessMessage.getFrom(), editedBuinessMessage.getText());
        } else if (update.hasDeletedBusinessMessage()) {
            return new UpdateHolder<>(update.getUpdateId(), update, update.getDeletedBusinessMessages(), UpdateType.DELETED_BUSINESS_MESSAGES, null, null);
        } else if (update.hasPaidMediaPurchased()) {
            PaidMediaPurchased paidMediaPurchased = update.getPaidMediaPurchased();
            return new UpdateHolder<>(update.getUpdateId(), update, paidMediaPurchased, UpdateType.PURCHASED_PAID_MEDIA, null, paidMediaPurchased.getPaidMediaPayload());
        } else {
            return new UpdateHolder<>(update.getUpdateId(), update, update, UpdateType.UPDATE, null, null);
        }
    }
}
