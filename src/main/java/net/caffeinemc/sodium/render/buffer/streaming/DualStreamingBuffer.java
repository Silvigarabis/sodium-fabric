package net.caffeinemc.sodium.render.buffer.streaming;

import java.nio.ByteBuffer;
import java.util.EnumSet;
import java.util.Set;

import net.caffeinemc.gfx.api.buffer.*;
import net.caffeinemc.gfx.api.device.RenderDevice;

public class DualStreamingBuffer extends SectionedStreamingBuffer {
    private ImmutableBuffer deviceResidentBuffer;

    public DualStreamingBuffer(RenderDevice device, int alignment, int sectionCapacity, int sectionCount, Set<MappedBufferFlags> extraFlags) {
        super(device, alignment, sectionCapacity, sectionCount, extraFlags);

        this.deviceResidentBuffer = device.createBuffer(
                sectionCapacity,
                EnumSet.noneOf(ImmutableBufferFlags.class)
        );
    }

    @Override
    public boolean resizeIfNeeded(WritableSection currentSection, int sectionCapacity, boolean copyContents) {
        // don't need to copy contents of staging buffer, only the device-resident buffer
        boolean resized = super.resizeIfNeeded(currentSection, sectionCapacity, false);
        if (resized) {
            var newBuffer = this.device.createBuffer(
                    this.sectionCapacity,
                    EnumSet.noneOf(ImmutableBufferFlags.class)
            );

            if (copyContents) {
                this.device.copyBuffer(
                        this.deviceResidentBuffer,
                        newBuffer,
                        0,
                        0,
                        this.deviceResidentBuffer.capacity()
                );
            }

            this.device.deleteBuffer(this.deviceResidentBuffer);

            this.deviceResidentBuffer = newBuffer;
        }
        return resized;
    }

    @Override
    public Buffer getBufferObject() {
        return this.deviceResidentBuffer;
    }

    @Override
    public long getDeviceUsedMemory() {
        return super.getDeviceUsedMemory() + this.deviceResidentBuffer.capacity();
    }

    @Override
    public long getDeviceAllocatedMemory() {
        return super.getDeviceAllocatedMemory() + this.deviceResidentBuffer.capacity();
    }

    @Override
    public void delete() {
        super.delete();
        this.device.deleteBuffer(this.deviceResidentBuffer);
    }

    @Override
    protected WritableSection createSection(ByteBuffer view, long offset) {
        return new DualSectionImpl(this.buffer, view, offset);
    }

    protected class DualSectionImpl extends SectionImpl {

        public DualSectionImpl(MappedBuffer buffer, ByteBuffer view, long offset) {
            super(buffer, view, offset);
        }

        @Override
        public void flushPartial() {
            long length = this.view.position() - this.lastFlushEndPos;
            if (length > 0) {
                long stagingBufferOffset = this.offset + this.lastFlushEndPos;

                this.buffer.flush(
                        stagingBufferOffset,
                        length
                );

                DualStreamingBuffer.this.device.copyBuffer(
                        this.buffer,
                        DualStreamingBuffer.this.deviceResidentBuffer,
                        stagingBufferOffset,
                        this.lastFlushEndPos,
                        length
                );

                this.lastFlushEndPos += length;
            }
        }

        @Override
        public void flushFull() {
            super.flushFull();

            DualStreamingBuffer.this.device.copyBuffer(
                    this.buffer,
                    DualStreamingBuffer.this.deviceResidentBuffer,
                    this.offset,
                    0,
                    this.view.capacity()
            );
        }

        @Override
        public long getOffset() {
            return 0; // FIXME: move this lol
        }
    }
}
