FROM archlinux:base-devel AS build
RUN pacman -Syu --noconfirm \
    rust && \
    cargo install apkeep

FROM golang:trixie AS build-go
RUN git clone https://github.com/xPaw/DumpStrings.git && \
    cd DumpStrings && \
    go install .

FROM archlinux:base
RUN pacman -Syu --noconfirm \
    git \
    python \
    python-pip \
    jre-openjdk \
    jadx \
    which \
    unzip \
    imagemagick \
    perl-xml-xpath
RUN python -m venv /data/.venv && \
. /data/.venv/bin/activate && \
pip install --upgrade git+https://github.com/P1sec/hermes-dec

ENV PATH=/bin/vendor_perl:/data/.venv/bin:$PATH
COPY --from=build /root/.cargo/bin/* /usr/local/bin/
COPY --from=build-go /go/bin/DumpStrings /usr/local/bin/
COPY setup.sh /data/setup.sh
CMD [ "/data/setup.sh" ]