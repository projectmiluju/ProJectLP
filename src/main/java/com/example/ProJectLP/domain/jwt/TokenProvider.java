package com.example.ProJectLP.domain.jwt;

import com.example.ProJectLP.domain.member.Member;
import com.example.ProJectLP.domain.refreshToken.RefreshToken;
import com.example.ProJectLP.dto.request.TokenDto;
import com.example.ProJectLP.dto.response.ResponseDto;
import com.example.ProJectLP.service.RefreshTokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Component
public class TokenProvider {

    private static final String AUTHORITIES_KEY = "auth";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * 300;            //300분
    private static final long REFRESH_TOKEN_EXPRIRE_TIME = 1000 * 60 * 60 * 24;     //1일

    private final Key key;

    @Autowired
    private RefreshTokenService refreshTokenService;

    public TokenProvider(@Value("${jwt.secret}") String secretKey,
                         RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public TokenDto generateTokenDto(Member member) {
        long now = (new Date().getTime());

        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(member.getUsername())
                .claim(AUTHORITIES_KEY, Authority.ROLE_MEMBER.toString())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + REFRESH_TOKEN_EXPRIRE_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshTokenObject = new RefreshToken(refreshToken, member.getId());

        refreshTokenService.setDataExpire(refreshTokenObject.getRefreshToken(),refreshTokenObject.getMemberId().toString(), 1 * 60 * 60 * 24);

        return TokenDto.builder()
                .grantType(BEARER_PREFIX)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();

    }

    public Member getMemberFromAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            return null;
        }
        return ((UserDetailsImpl) authentication.getPrincipal()).getMember();
    }

    public boolean validateToken(String refreshToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(refreshToken);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT signature");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token");
        } catch (IllegalArgumentException e) {
            log.info("JWT claims is empty");
        }
        return false;
    }



    @Transactional
    public ResponseDto<?> deleteRefreshToken(HttpServletRequest request, Member member) {

        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("400", "Token does not exist");
        }
        if (Objects.equals(refreshTokenService.getData(request.getHeader("RefreshToken")), member.getId().toString()))
        {
            refreshTokenService.deleteData(request.getHeader("RefreshToken"));
        } else return ResponseDto.fail("400", "Token does not match member");

        return ResponseDto.success("success");
    }
}
