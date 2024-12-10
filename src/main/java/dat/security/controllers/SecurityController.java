package dat.security.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.nimbusds.jose.JOSEException;
import dat.utils.Utils;
import dat.config.HibernateConfig;
import dat.security.daos.ISecurityDAO;
import dat.security.daos.SecurityDAO;
import dat.security.entities.User;
import dat.security.exceptions.ApiException;
import dat.security.exceptions.NotAuthorizedException;
import dat.security.exceptions.ValidationException;
import dk.bugelhartmann.ITokenSecurity;
import dk.bugelhartmann.TokenSecurity;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Handler;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Set;

public class SecurityController implements ISecurityController {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ITokenSecurity tokenSecurity = new TokenSecurity();
    private static ISecurityDAO securityDAO;
    private static SecurityController instance;
    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);

    private SecurityController() {
    }

    public static SecurityController getInstance() {
        if (instance == null) {
            instance = new SecurityController();
            securityDAO = new SecurityDAO(HibernateConfig.getEntityManagerFactory());
        }
        return instance;
    }

    @Override
    public Handler login() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                UserDTO user = ctx.bodyAsClass(UserDTO.class);
                UserDTO verifiedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());
                String token = createToken(verifiedUser);
                ctx.status(200).json(returnObject.put("token", token).put("msg", "Login successful"));
            } catch (EntityNotFoundException | ValidationException e) {
                ctx.status(401).json(returnObject.put("msg", "Wrong username or password"));
            } catch (Exception e) {
                ctx.status(500).json(returnObject.put("msg", "Internal server error"));
                logger.error("Error in login: " + e.getMessage());
            }
        };
    }

    @Override
    public Handler register() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                UserDTO userInput = ctx.bodyAsClass(UserDTO.class);
                User user = securityDAO.createUser(userInput.getUsername(), userInput.getPassword());
                ctx.status(201).json(returnObject.put("msg", "User created successfully"));
            } catch (EntityExistsException e) {
                ctx.status(409).json(returnObject.put("msg", "User already exists"));
            } catch (Exception e) {
                ctx.status(500).json(returnObject.put("msg", "Internal server error"));
                logger.error("Error in register: " + e.getMessage());
            }
        };
    }

    @Override
    public Handler authenticate() {
        return (ctx) -> {
            if (ctx.method().toString().equals("OPTIONS")) {
                ctx.status(200);
                return;
            }

            String header = ctx.header("Authorization");
            if (header == null) {
                throw new UnauthorizedResponse("Authorization header missing");
            }

            String[] headerParts = header.split(" ");
            if (headerParts.length != 2 || !headerParts[0].equals("Bearer")) {
                throw new UnauthorizedResponse("Authorization header malformed");
            }

            String token = headerParts[1];
            UserDTO verifiedTokenUser = verifyToken(token);

            if (verifiedTokenUser == null) {
                throw new UnauthorizedResponse("Invalid User or Token");
            }

            ctx.attribute("user", verifiedTokenUser);
        };
    }

    @Override
    public boolean authorize(UserDTO userDTO, Set<RouteRole> allowedRoles) {
        return userDTO.getRoles().stream().anyMatch(allowedRoles::contains);
    }

    @Override
    public String createToken(UserDTO user) throws Exception {
        try {
            boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
            String ISSUER;
            String TOKEN_EXPIRE_TIME;
            String SECRET_KEY;

            if (IS_DEPLOYED) {
                ISSUER = System.getenv("ISSUER");
                TOKEN_EXPIRE_TIME = System.getenv("TOKEN_EXPIRE_TIME");
                SECRET_KEY = System.getenv("SECRET_KEY");
            } else {
                ISSUER = Utils.getPropertyValue("ISSUER", "config.properties");
                TOKEN_EXPIRE_TIME = Utils.getPropertyValue("TOKEN_EXPIRE_TIME", "config.properties");
                SECRET_KEY = Utils.getPropertyValue("SECRET_KEY", "config.properties");
            }
            return tokenSecurity.createToken(user, ISSUER, TOKEN_EXPIRE_TIME, SECRET_KEY);
        } catch (Exception e) {
            logger.error("Error creating token: " + e.getMessage());
            throw new ApiException(500, "Could not create token");
        }
    }

    @Override
    public UserDTO verifyToken(String token) {
        try {
            boolean IS_DEPLOYED = (System.getenv("DEPLOYED") != null);
            String SECRET = IS_DEPLOYED ? System.getenv("SECRET_KEY") : Utils.getPropertyValue("SECRET_KEY", "config.properties");

            if (tokenSecurity.tokenIsValid(token, SECRET) && tokenSecurity.tokenNotExpired(token)) {
                return tokenSecurity.getUserWithRolesFromToken(token);
            } else {
                throw new NotAuthorizedException(403, "Token is not valid");
            }
        } catch (ParseException | JOSEException | NotAuthorizedException e) {
            logger.error("Error verifying token: " + e.getMessage());
            throw new ApiException(HttpStatus.UNAUTHORIZED.getCode(), "Unauthorized. Could not verify token");
        }
    }

    public Handler addRole() {
        return (ctx) -> {
            ObjectNode returnObject = objectMapper.createObjectNode();
            try {
                String newRole = ctx.bodyAsClass(ObjectNode.class).get("role").asText();
                UserDTO user = ctx.attribute("user");
                if (user == null) {
                    throw new UnauthorizedResponse("User not authenticated");
                }
                User updatedUser = securityDAO.addRole(user, newRole);
                ctx.status(200).json(returnObject.put("msg", "Role " + newRole + " added to user"));
            } catch (EntityNotFoundException e) {
                ctx.status(404).json(returnObject.put("msg", "User not found"));
            } catch (Exception e) {
                ctx.status(500).json(returnObject.put("msg", "Internal server error"));
                logger.error("Error adding role: " + e.getMessage());
            }
        };
    }
}