package vn.hust.social.backend.dto.chat;

import vn.hust.social.backend.dto.MediaDTO;

import vn.hust.social.backend.dto.MessageDTO;

import java.util.List;

public record WsMessageResponse(
                MessageDTO message,
                List<MediaDTO> medias) {
}
