package tn.esprit.tpfoyer17.configurations;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@Aspect
@Slf4j
public class AOPConfig {

    @Around("execution(* tn.esprit.tpfoyer17.services.ReservationService.ajouterReservation(..))")
    public Object temps(ProceedingJoinPoint pjp) throws Throwable {
        Instant start = Instant.now();
        try {
            return pjp.proceed();
        } finally {
            long elapsedMs = Duration.between(start, Instant.now()).toMillis();
            log.info("temps d'execution = {} ms", elapsedMs);
        }
    }
}