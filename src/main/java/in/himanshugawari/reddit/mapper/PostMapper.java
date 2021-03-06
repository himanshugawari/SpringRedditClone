package in.himanshugawari.reddit.mapper;

import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.marlonlom.utilities.timeago.TimeAgo;

import in.himanshugawari.reddit.dto.PostRequest;
import in.himanshugawari.reddit.dto.PostResponse;
import in.himanshugawari.reddit.model.Post;
import in.himanshugawari.reddit.model.Subreddit;
import in.himanshugawari.reddit.model.User;
import in.himanshugawari.reddit.model.Vote;
import in.himanshugawari.reddit.model.VoteType;
import in.himanshugawari.reddit.repository.CommentRepository;
import in.himanshugawari.reddit.repository.VoteRepository;
import in.himanshugawari.reddit.service.AuthService;
/*
@Mapper(componentModel = "spring")
public interface PostMapper {

	@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
    @Mapping(target = "description", source = "postRequest.description")
    @Mapping(target = "subreddit", source = "subreddit")
	Post map(PostRequest postRequest, Subreddit subreddit, User user);

	@Mapping(target = "id", source = "postId")
    @Mapping(target = "subredditName", source = "subreddit.name")
    @Mapping(target = "userName", source = "user.username")
	PostResponse mapToDto(Post post);
}
*/

@Mapper(componentModel = "spring")
public abstract class PostMapper {

	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private AuthService authService;
	@Autowired
	private VoteRepository voteRepository;

	@Mapping(target = "createdDate", expression = "java(java.time.Instant.now())")
	@Mapping(target = "description", source = "postRequest.description")
	@Mapping(target = "subreddit", source = "subreddit")
	@Mapping(target = "user", source = "user")
	@Mapping(target = "voteCount", constant = "0")
	public abstract Post map(PostRequest postRequest, Subreddit subreddit, User user);

	@Mapping(target = "id", source = "postId")
	@Mapping(target = "postName", source = "postName")
	@Mapping(target = "description", source = "description")
	@Mapping(target = "url", source = "url")
	@Mapping(target = "subredditName", source = "subreddit.name")
	@Mapping(target = "userName", source = "user.username")
	@Mapping(target = "commentCount", expression = "java(commentCount(post))")
	@Mapping(target = "duration", expression = "java(getDuration(post))")
	@Mapping(target = "upVote", expression = "java(isPostUpVoted(post))")
	@Mapping(target = "downVote", expression = "java(isPostDownVoted(post))")
	public abstract PostResponse mapToDto(Post post);

	Integer commentCount(Post post) {
		return commentRepository.findByPost(post).size();
	}

	String getDuration(Post post) {
		return TimeAgo.using(post.getCreatedDate().toEpochMilli());
	}

	boolean isPostUpVoted(Post post) {
		return checkVoteType(post, VoteType.UPVOTE);
	}

	boolean isPostDownVoted(Post post) {
		return checkVoteType(post, VoteType.DOWNVOTE);
	}

	public boolean checkVoteType(Post post, VoteType voteType) {
		if (authService.isLoggedIn()) {
			Optional<Vote> voteForPostByUser = voteRepository.findTopByPostAndUserOrderByVoteIdDesc(post,
					authService.getCurrentUser());
			return voteForPostByUser.filter(vote -> vote.getVoteType().equals(voteType)).isPresent();
		}
		return false;
	}
}
