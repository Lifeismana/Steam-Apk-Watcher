FROM archlinux:base-devel AS build
RUN pacman -Syu --noconfirm \
    rust && \
    cargo install apkeep

FROM archlinux:base
RUN pacman -Syu --noconfirm \
    git \
    python \
    python-pip \
    jadx \
    unzip
RUN python -m venv /data/.venv && \
. /data/.venv/bin/activate && \
pip install --upgrade git+https://github.com/P1sec/hermes-dec

ENV PATH=/data/.venv/bin:$PATH
COPY --from=build /root/.cargo/bin/* /usr/local/bin/
COPY setup.sh /data/setup.sh
CMD [ "/data/setup.sh" ]