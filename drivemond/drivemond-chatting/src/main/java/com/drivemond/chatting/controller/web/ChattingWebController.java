package com.drivemond.chatting.controller.web;

import com.drivemond.chatting.entity.ChannelList;
import com.drivemond.chatting.entity.ChannelUser;
import com.drivemond.chatting.repository.ChannelListRepository;
import com.drivemond.chatting.service.ChannelService;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/chatting")
@RequiredArgsConstructor
public class ChattingWebController {

    private final ChannelListRepository channelListRepo;
    private final ChannelService channelService;

    @GetMapping
    public String index(@RequestParam(defaultValue = "0") int page, Model model) {
        Page<ChannelList> channels = channelListRepo.findAll(
                PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "updatedAt")));

        // Build a map of channelId → "User1, User2" for display
        Map<java.util.UUID, String> memberMap = channels.getContent().stream()
                .collect(Collectors.toMap(
                        ChannelList::getId,
                        ch -> channelService.getChannelMembers(ch.getId()).stream()
                                .map(ChannelUser::getUserId)
                                .map(id -> id.toString().substring(0, 8) + "…")
                                .collect(Collectors.joining(", "))
                ));

        model.addAttribute("channels", channels);
        model.addAttribute("memberMap", memberMap);
        return "admin/chatting/index";
    }
}
