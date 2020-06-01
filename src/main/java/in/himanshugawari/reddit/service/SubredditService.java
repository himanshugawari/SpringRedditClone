package in.himanshugawari.reddit.service;

import org.springframework.stereotype.Service;

import in.himanshugawari.reddit.dto.SubredditDto;
import in.himanshugawari.reddit.model.Subreddit;
import in.himanshugawari.reddit.repository.SubredditRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
//@Slf4j
public class SubredditService {

	private final SubredditRepository subredditRepository;

	public SubredditDto save(SubredditDto subredditDto) {
		Subreddit save = subredditRepository.save(mapSubredditDto(subredditDto));
		subredditDto.setId(save.getId());
		return subredditDto;
	}

	private Subreddit mapSubredditDto(SubredditDto subredditDto) {
		return Subreddit.builder().name(subredditDto.getName()).description(subredditDto.getDescription()).build();
	}
}