FROM debian:bookworm-slim AS base
RUN echo '\nAcquire::Retries "100";\nAcquire::https::Timeout "240";\nAcquire::http::Timeout "240";\nAPT::Install-Recommends "false";\nAPT::Install-Suggests "false";\n' > /etc/apt/apt.conf.d/99custom && \
    apt-get update && apt-get install -y \
    git \
    ca-certificates \
    python3-pip \
    && rm -rf /var/lib/apt/lists/* \
    && pip install --upgrade git+https://github.com/P1sec/hermes-dec
