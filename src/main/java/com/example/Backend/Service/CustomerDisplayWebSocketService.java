package com.example.Backend.Service;

import com.example.Backend.Dto.CustomerDisplayDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Service responsible for broadcasting customer display updates over WebSocket.
 */
@Service
@RequiredArgsConstructor
public class CustomerDisplayWebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public void broadcastDisplay(CustomerDisplayDto dto) {
        if (dto == null || dto.getBranchid() == null) {
            return;
        }

        String destination = String.format("/topic/customer-display/%d", dto.getBranchid());
        messagingTemplate.convertAndSend(destination, dto);
    }
}
